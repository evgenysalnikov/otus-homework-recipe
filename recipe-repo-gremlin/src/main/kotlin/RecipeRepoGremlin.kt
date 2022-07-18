package com.salnikoff.recipe.repo.gremlin

import com.benasher44.uuid.uuid4
import com.salnikoff.recipe.common.helpers.errorAdministration
import com.salnikoff.recipe.common.helpers.errorConcurrency
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.repo.*
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_DESCRIPTION
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_LOCK
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_OWNER_ID
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_STEPS
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_TITLE
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.RESULT_LOCK_FAILURE
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.RESULT_SUCCESS
import com.salnikoff.recipe.repo.gremlin.exceptions.DbDuplicatedElementsException
import com.salnikoff.recipe.repo.gremlin.exceptions.WrongIdTypeException
import com.salnikoff.recipe.repo.gremlin.exceptions.WrongResponseFromDb
import com.salnikoff.recipe.repo.gremlin.mappers.addRecipe
import com.salnikoff.recipe.repo.gremlin.mappers.label
import com.salnikoff.recipe.repo.gremlin.mappers.toRecipe
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.exception.ResponseException
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.TextP
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.*
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.VertexProperty
import kotlin.time.DurationUnit

class RecipeRepoGremlin(
    private val hosts: String,
    private val port: Int = 8182,
    private val enableSsl: Boolean = false,
    initObjects: List<Recipe> = emptyList(),
    initRepo: ((GraphTraversalSource) -> Unit)? = null,
    val randomUuid: () -> String = { uuid4().toString() }
) : IRecipeRepository {

    val initializedObjects: List<Recipe>

    private val cluster by lazy {
        Cluster.build().apply {
            addContactPoints(*hosts.split(Regex("\\s*,\\s*")).toTypedArray())
            port(port)
            enableSsl(enableSsl)
        }.create()
    }
    private val g by lazy { AnonymousTraversalSource.traversal().withRemote(DriverRemoteConnection.using(cluster)) }

    private val recipeRequirementRepo by lazy {
        RecipeRequirementGremlinRepo(
            hosts = hosts,
            port = port,
            enableSsl = enableSsl,
            initObjects = emptyList(),
            initRepo = { g -> g.V().has("requirement").drop().iterate() },
        )
    }

    init {
        if (initRepo != null) {
            initRepo(g)
        }
        initializedObjects = initObjects.map { recipe ->
            val newRecipeId = save(recipe)
            recipe.id = RecipeId(newRecipeId)
            recipe
        }
    }

    private fun save(recipe: Recipe): String {
        val recipeId = g.addV(recipe.label())
            .property(VertexProperty.Cardinality.single, FIELD_TITLE, recipe.title)
            .property(VertexProperty.Cardinality.single, FIELD_DESCRIPTION, recipe.description)
            .property(
                VertexProperty.Cardinality.single,
                RecipeGremlinConst.FIELD_DURATION, recipe.duration.toLong(DurationUnit.SECONDS))
            .property(VertexProperty.Cardinality.single, FIELD_OWNER_ID, recipe.ownerId.asString())
            .property(VertexProperty.Cardinality.single, RecipeGremlinConst.FIELD_VISIBILITY, recipe.visibility.name)
            .property(VertexProperty.Cardinality.single, FIELD_STEPS, recipe.steps)
            .property(VertexProperty.Cardinality.single, FIELD_LOCK, recipe.lock.asString())
            .next()?.id().toString()

        recipe.requirements.map { recipeRequirement ->
            val reqId = recipeRequirementRepo.createRecipeRequirement(recipeRequirement)
            val rqv = g.V(reqId).next()
            g.V(recipeId)
                .addE("depends").from(rqv)
                .property("weight", 1)
                .iterate()
        }

        return recipeId
    }

    private fun saveOld(recipe: Recipe): String = g.addV(recipe.label()).addRecipe(recipe)?.next()?.id() as? String ?: ""

    override suspend fun createRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        val key = randomUuid()
        val recipe = rq.recipe.copy(id = RecipeId(key), lock = RecipeLock(randomUuid()))
        val id = save(recipe)
            .let {
                when (it) {
                    is String -> it
                    else -> return DbRecipeResponse(
                        result = null, isSuccess = false,
                        errors = listOf(
                            errorAdministration(
                                violationCode = "badDbResponse",
                                description = "Unexpected result got. Please, contact administrator",
                                exception = WrongIdTypeException(
                                    "createAd for ${this@RecipeRepoGremlin::class} " +
                                            "returned id = '$it' that is not admitted by the application"
                                )
                            )
                        )
                    )
                }
            }

        return DbRecipeResponse(
            result = recipe.copy(id = RecipeId(id)),
            isSuccess = true,
        )
    }

    override suspend fun readRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        val key = rq.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        val dbRes = try {
            g.V(key)//todo: realize by project()
                .elementMap<Any>()
                .toList()
        } catch (e: Throwable) {
            if (e is ResponseException || e.cause is ResponseException) {
                return resultErrorNotFound
            }
            return DbRecipeResponse(
                result = null,
                isSuccess = false,
                errors = listOf(e.asRecipeError())
            )
        }

        when (dbRes.size) {
            0 -> return resultErrorNotFound
            1 -> {
                val recipe = dbRes.first().toRecipe()
                recipe.requirements = getRecipeRequirements(key)
                recipe.permissionsClient.addAll(getRecipePermissions(key))

                return DbRecipeResponse(
                    result = recipe,
                    isSuccess = true,
                )
            }
            else -> return DbRecipeResponse(
                result = null,
                isSuccess = false,
                errors = listOf(
                    errorAdministration(
                        violationCode = "duplicateObjects",
                        description = "Database consistency failure",
                        exception = DbDuplicatedElementsException("Db contains multiple elements for id = '$key'")
                    )
                )
            )
        }
    }

    private fun getRecipeRequirements(recipeKey: String): List<RecipeRequirement> {
        return g.V(recipeKey).`in`("depends").elementMap<Any>().toList().map {
            RecipeRequirement(it["name"].toString())
        }
    }

    private fun getRecipePermissions(recipeKey: String): MutableSet<RecipePermissionClient> {
        return g.V(recipeKey).`in`("havePermissions").elementMap<Any>().toList().map {
            RecipePermissionClient.valueOf(it["name"].toString())
        }.toMutableSet()
    }

    override suspend fun updateRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        val key = rq.recipe.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        val oldLock = rq.recipe.lock.takeIf { it != RecipeLock.NONE }
        val newLock = RecipeLock(randomUuid())
        val newRecipe = rq.recipe.copy(lock = newLock)
        val dbRes = try {
            g
                .V(key)
                .`as`("a")
                .choose(
                    select<Vertex, Any>("a")
                        .values<String>(FIELD_LOCK)
                        .`is`(oldLock?.asString()),
                    select<Vertex, Vertex>("a").addRecipe(newRecipe),
                    select<Vertex, Vertex>("a")
                ).elementMap<Any>().toList()
        } catch (e: Throwable) {
            if (e is ResponseException || e.cause is ResponseException) {
                return resultErrorNotFound
            }
            return DbRecipeResponse(
                result = null,
                isSuccess = false,
                errors = listOf(e.asRecipeError())
            )
        }
        val recipeResult = dbRes.firstOrNull()?.toRecipe()
        when {
            dbRes.size == 0 -> return resultErrorNotFound
            dbRes.size == 1 && recipeResult?.lock == oldLock -> return resultErrorConcurrent
            dbRes.size == 1 && recipeResult?.lock == newLock -> return DbRecipeResponse(
                result = dbRes.first().toRecipe(),
                isSuccess = true,
            )
            else -> return DbRecipeResponse(
                result = null,
                isSuccess = false,
                errors = listOf(
                    errorAdministration(
                        violationCode = "duplicateObjects",
                        description = "Database consistency failure",
                        exception = DbDuplicatedElementsException("Db contains multiple elements for id = '$key'")
                    )
                )
            )
        }
    }

    override suspend fun deleteRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        val key = rq.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        val oldLock = rq.lock.takeIf { it != RecipeLock.NONE }?.asString() ?: return resultErrorEmptyLock
        val readResult = readRecipe(rq)
        if (!readResult.isSuccess) return readResult
        val result = g
            .V(key)
            .`as`("a")
            .choose(
                select<Vertex, Any>("a")
                    .values<String>(FIELD_LOCK)
                    .`is`(oldLock),
                select<Vertex, String>("a").drop().inject(RESULT_SUCCESS),
                constant(RESULT_LOCK_FAILURE)
            ).toList().firstOrNull()

        return when (result) {
            RESULT_SUCCESS -> readResult
            RESULT_LOCK_FAILURE -> resultErrorConcurrent
            null -> resultErrorNotFound
            else -> throw WrongResponseFromDb("Unsupported response '$result' from DB Gremliln for ${this::deleteRecipe::class}")
        }
    }

    override suspend fun searchRecipe(rq: DbRecipeFilterRequest): DbRecipesResponse {
        val result = try {
            g.V()
                .apply { rq.ownerId.takeIf { it != UserId.NONE }?.also { has(FIELD_OWNER_ID, it.asString()) } }
                .apply { rq.titleFilter.takeIf { it.isNotBlank() }?.also { has(FIELD_TITLE, TextP.containing(it)) } }
                .apply { rq.descriptionFilter.takeIf { it.isNotBlank() }?.also { has(FIELD_DESCRIPTION, TextP.containing(it)) } }
                .apply { rq.stepsFilter.takeIf { it.isNotBlank() }?.also { has(FIELD_STEPS, TextP.containing(it)) } }
                .elementMap<Any>()
                .toList()
        } catch (e: Throwable) {
            return DbRecipesResponse(
                isSuccess = false,
                result = null,
                errors = listOf(e.asRecipeError())
            )
        }
        return DbRecipesResponse(
            result = result.map { it.toRecipe().let { recipe ->
                recipe.requirements = getRecipeRequirements(recipe.id.asString())
                recipe.permissionsClient.addAll(getRecipePermissions(recipe.id.asString()))
                recipe
            } },
            isSuccess = true
        )
    }

    companion object {
        val resultErrorEmptyId = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                RecipeError(field = "id", message = "Id must be not null or empty")
            )
        )

        val resultErrorNotFound = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                RecipeError(field = "id", message = "Not Found")
            )
        )

        val resultErrorEmptyLock = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                RecipeError(
                    field = "lock",
                    message = "Lock must be provided"
                )
            )
        )

        val resultErrorConcurrent = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                errorConcurrency(
                    violationCode = "changed",
                    description = "Object has changed during request handling"
                )
            )
        )
    }
}