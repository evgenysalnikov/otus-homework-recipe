package com.salnikoff.recipe.repo.gremlin

import com.benasher44.uuid.uuid4
import com.salnikoff.recipe.common.models.RecipeRequirement
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.exception.ResponseException
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

internal class RecipeRequirementGremlinRepo(
    private val hosts: String,
    private val port: Int = 8182,
    private val enableSsl: Boolean = false,
    initObjects: List<RecipeRequirement> = emptyList(),
    initRepo: ((GraphTraversalSource) -> Unit)? = null,
    val randomUuid: () -> String = { uuid4().toString() }
) {

    val initializedObjects: List<RecipeRequirement>

    private val cluster by lazy {
        Cluster.build().apply {
            addContactPoints(*hosts.split(Regex("\\s*,\\s*")).toTypedArray())
            port(port)
            enableSsl(enableSsl)
        }.create()
    }
    private val g by lazy { AnonymousTraversalSource.traversal().withRemote(DriverRemoteConnection.using(cluster)) }

    init {
        if (initRepo != null) {
            initRepo(g)
        }
        initializedObjects = initObjects.map {
            val id = save(it)
            RecipeRequirement(it.asString())
        }
    }

    private fun save(req: RecipeRequirement): String = g.addV("requirement").property("name", req.asString())
        .next()?.id().toString()

    fun createRecipeRequirement(req: RecipeRequirement): String? {
        return when (val search = searchRecipeRequirement(req.asString())) {
            null -> g.addV("requirement").property("name", req.asString()).next()?.id().toString()
            else -> search
        }
//        return g.addV("requirement").property("name", req.asString()).next()?.id().toString()
    }

    fun readRecipeRequirement(key: String): RecipeRequirement? {
        val requirements = g.V(key).elementMap<Any>().toList()
        return when (requirements.size) {
            0 -> null
            1 -> RecipeRequirement(requirements.first()["name"].toString())
            else -> throw Exception("duplicate requirement found")
        }
    }

    fun deleteRecipeRequirement(key: String): Boolean {
        return try {
            val req = g.V(key).next()
            val result = g.V(key).drop().inject(req).toList()
            true
        } catch (e: NoSuchElementException) {
            false
        } catch (e: Throwable) {
            //log
            println(e)
            false
//            throw e;
        }
    }

    fun searchRecipeRequirement(name: String): String? {
        return try {
            val search = g.V().has("requirement", "name", name).toList()
            when (search.size) {
                0 -> null
                1 -> search.first().id().toString()
                else -> {
                    println("duplicate found")
                    null
                }
            }
        } catch (e: NoSuchElementException) {
            null
        } catch (e: ResponseException) {
            println(e.message)
            null
        } catch (e: Throwable) {
            println(e)
            null
        }

    }
}
