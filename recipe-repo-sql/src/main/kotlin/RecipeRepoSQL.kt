package com.salnikoff.recipe.repo.sql

import com.salnikoff.recipe.common.helpers.errorConcurrency
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.repo.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import java.util.*

class RecipeRepoSQL(
    url: String = "jdbc:postgresql://localhost:5432/recipedb",
    user: String = "recipe",
    password: String = "recipe-pass",
    schema: String = "recipe",
    initObjects: Collection<Recipe> = emptyList(),
    private val newRecipeUpdateLock: RecipeLock? = null
) : IRecipeRepository {

    private val db by lazy {
        SqlConnector(url, user, password, schema).connect(
            UsersTable,
            RequirementTable,
            RecipeTable,
            RecipeRequirementTable
        )
    }
    private val mutex = Mutex()

    init {
        initObjects.forEach {
            save(it)
        }
    }

    private fun saveRequirement(recipeRequirement: RecipeRequirement): String {
        val result = transaction(db) {
            if (
                RequirementTable.slice(RequirementTable.id).select {
                    RequirementTable.name eq recipeRequirement.asString()
                }.count() == 1L
            ) {
                RequirementTable.select {
                    RequirementTable.name eq recipeRequirement.asString()
                }.single()[RequirementTable.id]
            } else {
                RequirementTable.insert {
                    it[id] = generateUuidAsStringFixedSize()
                    it[RequirementTable.name] = recipeRequirement.asString()
                } get RequirementTable.id
            }
        }

        return result.toString()
    }

    private fun addRequirementToRecipe(recipe: Recipe, requirement: RecipeRequirement): Boolean {
        val result = transaction(db) {
            val requirementId = saveRequirement(requirement)
            println("requirement $requirementId")

            RecipeRequirementTable.insert {
                it[this.recipe] = RecipeTable.slice(RecipeTable.id).select { RecipeTable.id eq recipe.id.asString() }
                it[RecipeRequirementTable.requirement] =
                    RequirementTable.slice(RequirementTable.id).select { RequirementTable.id eq requirementId }
            }

        }

        return result.insertedCount > 0
    }

    private fun deleteRequirementFromRecipe(recipe: Recipe, requirement: RecipeRequirement): Boolean {
        transaction(db) {
            RequirementTable.select { RequirementTable.name eq requirement.asString() }.single()
        }.let {
            transaction(db) {
                RecipeRequirementTable.deleteWhere {
                    (RecipeRequirementTable.requirement eq it[RequirementTable.id]) and (RecipeRequirementTable.recipe eq recipe.id.asString())
                }
            }
            return true
        }
    }

    private fun updateRequirements(
        recipe: Recipe,
        oldReq: List<RecipeRequirement>,
        newReq: List<RecipeRequirement>
    ): Boolean {
        var result = false

        transaction(db) {
            oldReq.minus(newReq).map { deleteRequirementFromRecipe(recipe, it) }
            newReq.minus(oldReq).map { addRequirementToRecipe(recipe, it) }
            result = true
        }

        return result
    }


    private fun save(item: Recipe): DbRecipeResponse {
        return safeTransaction({
            val realOwnerId = UsersTable.insertIgnore {
                if (item.ownerId != UserId.NONE) {
                    it[id] = item.ownerId.asString()
                }
            } get UsersTable.id

            val res = RecipeTable.insert {
                if (item.id != RecipeId.NONE) {
                    it[id] = item.id.asString()
                } else {
                    it[id] = generateUuidAsStringFixedSize()
                }
                it[title] = item.title
                it[description] = item.description
                it[duration] = item.duration.inWholeSeconds
                it[ownerId] = realOwnerId
                it[visibility] = item.visibility
                it[steps] = item.steps
                it[lock] = item.lock.asString()
            }

            val recipe = RecipeTable.from(res)

            item.requirements.map { recipeRequirement ->
                addRequirementToRecipe(recipe, recipeRequirement)
            }
            recipe.requirements = item.requirements

            DbRecipeResponse(recipe, true)
        }, {
            DbRecipeResponse(
                result = null,
                isSuccess = false,
                errors = listOf(RecipeError(message = message ?: localizedMessage))
            )
        })
    }

    override suspend fun createRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        val recipe = rq.recipe.copy(lock = RecipeLock(UUID.randomUUID().toString()))
        return mutex.withLock {
            save(recipe)
        }
    }

    override suspend fun readRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        return safeTransaction({
            val result = (RecipeTable innerJoin UsersTable).select { RecipeTable.id.eq(rq.id.asString()) }.single()

            val recipe = RecipeTable.from(result)

            val requirements = RecipeTable
                .innerJoin(RecipeRequirementTable)
                .innerJoin(RequirementTable)
                .slice(RequirementTable.name)
                .select { RecipeTable.id.eq(rq.id.asString()) }
                .map { RecipeRequirement(it[RequirementTable.name]) }

            recipe.requirements = requirements

            DbRecipeResponse(recipe, true)
        }, {
            val err = when (this) {
                is NoSuchElementException -> RecipeError(field = "id", message = "Not Found")
                is IllegalArgumentException -> RecipeError(message = "More than one element with the same id")
                else -> RecipeError(message = localizedMessage)
            }
            DbRecipeResponse(result = null, isSuccess = false, errors = listOf(err))
        })
    }

    override suspend fun updateRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        val key = rq.recipe.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        val oldLock = rq.recipe.lock.takeIf { it != RecipeLock.NONE }?.asString()
//        val newRecipe = rq.recipe.copy(lock = RecipeLock(UUID.randomUUID().toString()))
        val newRecipe = rq.recipe.copy(lock = newRecipeUpdateLock.takeIf { it != null } ?: RecipeLock(UUID.randomUUID().toString()))

        return mutex.withLock {
            safeTransaction({
                val local = RecipeTable.select { RecipeTable.id.eq(key) }.singleOrNull()?.let {
                    RecipeTable.from(it)
                } ?: return@safeTransaction resultErrorNotFound

                return@safeTransaction when (oldLock) {
                    null, local.lock.asString() -> updateDb(newRecipe)
                    else -> resultErrorConcurrent
                }
            }, {
                DbRecipeResponse(
                    result = null,
                    isSuccess = false,
                    errors = listOf(RecipeError(field = "id", message = "Not Found"))
                )
            })
        }
    }

    private fun updateDb(newRecipe: Recipe): DbRecipeResponse {
        UsersTable.insertIgnore {
            if (newRecipe.ownerId != UserId.NONE) {
                it[id] = newRecipe.ownerId.asString()
            }
        }

        RecipeTable.update({ RecipeTable.id.eq(newRecipe.id.asString()) }) {
            it[title] = newRecipe.title
            it[description] = newRecipe.description
            it[duration] = newRecipe.duration.inWholeSeconds
            it[ownerId] = newRecipe.ownerId.asString()
            it[visibility] = newRecipe.visibility
            it[steps] = newRecipe.steps
            it[lock] = newRecipe.lock.asString()
        }
        val result = RecipeTable.select { RecipeTable.id.eq(newRecipe.id.asString()) }.single()

        return DbRecipeResponse(result = RecipeTable.from(result), isSuccess = true)
    }

    override suspend fun deleteRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        val key = rq.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId

        return mutex.withLock {
            safeTransaction({
                val local = RecipeTable.select { RecipeTable.id.eq(key) }.single().let { RecipeTable.from(it) }
                if (local.lock == rq.lock) {
                    local.requirements =  RecipeTable
                        .innerJoin(RecipeRequirementTable)
                        .innerJoin(RequirementTable)
                        .slice(RequirementTable.name)
                        .select { RecipeTable.id.eq(rq.id.asString()) }
                        .map { RecipeRequirement(it[RequirementTable.name]) }

                    RecipeRequirementTable.deleteWhere { RecipeRequirementTable.recipe eq rq.id.asString() }
                    RecipeTable.deleteWhere { RecipeTable.id eq rq.id.asString() }
                    DbRecipeResponse(result = local, isSuccess = true)
                } else {
                    resultErrorConcurrent
                }
            }, {
                DbRecipeResponse(
                    result = null,
                    isSuccess = false,
                    errors = listOf(RecipeError(field = "id", message = "Not Found"))
                )
            })
        }
    }

    override suspend fun searchRecipe(rq: DbRecipeFilterRequest): DbRecipesResponse {
        return safeTransaction({
            // Select only if options are provided
            val results = (RecipeTable innerJoin UsersTable).select {
                (if (rq.ownerId == UserId.NONE) Op.TRUE else RecipeTable.ownerId eq rq.ownerId.asString()) and
                        (
                                if (rq.titleFilter.isBlank()) Op.TRUE else (RecipeTable.title like "%${rq.titleFilter}%") or
                                        (RecipeTable.description like "%${rq.titleFilter}%")
                                )
            }

            DbRecipesResponse(result = results.map {
                val recipe = RecipeTable.from(it)
                val requirements = RecipeTable
                    .innerJoin(RecipeRequirementTable)
                    .innerJoin(RequirementTable)
                    .slice(RequirementTable.name)
                    .select { RecipeTable.id.eq(recipe.id.asString()) }
                    .map { RecipeRequirement(it[RequirementTable.name]) }
                recipe.requirements = requirements
                recipe
            }, isSuccess = true)
        }, {
            DbRecipesResponse(result = emptyList(), isSuccess = false, listOf(RecipeError(message = localizedMessage)))
        })
    }

    /**
     * Transaction wrapper to safely handle caught exception and throw all sql-like exceptions. Also remove lot's of duplication code
     */
    private fun <T> safeTransaction(statement: Transaction.() -> T, handleException: Throwable.() -> T): T {
        return try {
            transaction(db, statement)
        } catch (e: SQLException) {
            throw e
        } catch (e: Throwable) {
            return handleException(e)
        }
    }

    companion object {
        val resultErrorEmptyId = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                RecipeError(
                    field = "id",
                    message = "Id must not be null or blank"
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
                ),
            )
        )
        val resultErrorNotFound = DbRecipeResponse(
            isSuccess = false,
            result = null,
            errors = listOf(
                RecipeError(
                    field = "id",
                    message = "Not Found"
                )
            )
        )
    }
}