package com.salnikoff.recipe.repo.gremlin.test

import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst
import com.salnikoff.recipe.repo.test.RepoRecipeSearchTest
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.*
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Transaction
import org.apache.tinkerpop.gremlin.structure.Vertex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OneMoreTest {

    private val cluster by lazy {
        Cluster.build().apply {
            addContactPoints(*ArcadeDbContainer.container.host.split(Regex("\\s*,\\s*")).toTypedArray())
            port(ArcadeDbContainer.container.getMappedPort(8182))
            enableSsl(false)
        }.create()
    }
    private val g by lazy { AnonymousTraversalSource.traversal().withRemote(DriverRemoteConnection.using(cluster)) }

    private fun getRequirement(name: String): String {
        return g.V()
            .hasLabel("requirement")
            .has("name", name)
            .fold()
            .coalesce(
                unfold(),
                addV<Element>("requirement").property("name", name)
            ).next().id().toString()
    }

    private fun createRecipe(title: String, description: String, requirements: List<String> = listOf()): String {
//        val tx: Transaction = g.tx()
//        val gtx: GraphTraversalSource = tx.begin()
//        try {
//            val requirementsResultIds: MutableList<String> = mutableListOf()
//            requirements.map { reqName ->
//                    .let { requirementsResultIds.add(it.toString()) }
//                gtx.V()
//                    .hasLabel("requirement")
//                    .has("name", reqName)
//                    .fold()
//                    .coalesce(
//                        unfold(),
//                        addV<Element>("requirement").property("name", reqName)
//                    ).next().id().let { requirementsResultIds.add(it.toString()) }
//            }
//            tx.commit()
//            println(requirementsResultIds)
//            return requirementsResultIds
//        } catch (e: Exception) {
//            println(e)
//            tx.rollback()
//        }
//
//        return ""

        return g
            .addV("recipe")
            .property("title", title)
            .property("description", description)
            .`as`("r")
            .select<Any>("r")
            .addE("depends")
            .from(
                V<Any>()
                    .hasLabel("requirement")
                    .has("name", "req100")
                    .fold()
                    .coalesce(
                        unfold(),
                        addV<Element>("requirement").property("name", "req100")
                    )
            )
            .property("weight", 1)
            .select<Vertex>("r")
            .addE("depends")
            .from(
                V<Any>()
                    .hasLabel("requirement")
                    .has("name", "req200")
                    .fold()
                    .coalesce(
                        unfold(),
                        addV<Element>("requirement").property("name", "req200")
                    )
            )
            .select<Vertex>("r")
            .next()
            .id().toString()

    }

    private fun readRecipe(key: String) {
        g.V(key)
            .`as`("recipe")
            .project<Any>(
                RecipeGremlinConst.FIELD_REQUIREMENT,
                RecipeGremlinConst.FIELD_PERMISSIONS_CLIENT,
                "recipe",
                "id",
                RecipeGremlinConst.FIELD_TITLE,
                RecipeGremlinConst.FIELD_DESCRIPTION,
                RecipeGremlinConst.FIELD_VISIBILITY,
                RecipeGremlinConst.FIELD_DURATION,
                RecipeGremlinConst.FIELD_STEPS,
                RecipeGremlinConst.FIELD_OWNER_ID,
                RecipeGremlinConst.FIELD_LOCK
            )
            .by(inE("depends").outV().values<Any>("name").fold())
            .by(inE("havePermissions").outV().values<Any>("name").fold())
            .by(elementMap<Vertex, Any>())
            .by(key)
            .by(RecipeGremlinConst.FIELD_TITLE)
            .by(RecipeGremlinConst.FIELD_DESCRIPTION)
            .by(RecipeGremlinConst.FIELD_VISIBILITY)
            .by(RecipeGremlinConst.FIELD_DURATION)
            .by(RecipeGremlinConst.FIELD_STEPS)
            .by(RecipeGremlinConst.FIELD_OWNER_ID)
            .by(RecipeGremlinConst.FIELD_LOCK)
            .toList()
            .let {
                println(it)
            }


    }

    @Test
    fun createTest() {
        val req1Name = "req1"
        val req2Name = "req2"
        val req3Name = "req3"

        val req1id = g.addV("requirement").property("name", "first").next().id()
        println(req1id)

        getRequirement(req1Name)
        getRequirement(req2Name)
        getRequirement(req3Name)

        getRequirement(req1Name)
        getRequirement(req2Name)
        getRequirement(req3Name)

        assertEquals(getRequirement(req1Name), getRequirement(req1Name))

        val reqCountResult = g.V()
            .hasLabel("requirement")
            .count()
            .next()
        println(reqCountResult)

        g.V()
            .hasLabel("recipe")
            .count()
            .next().let { println("recipe count: $it") }

        val cr = createRecipe("Pie", "PieDescription", listOf(req1Name, req3Name, "foo"))
        println(cr)

        g.V()
            .hasLabel("recipe")
            .count()
            .next().let { println("recipe count: $it") }

//        println(g.V(cr).elementMap<Any>().toList())
//        println(g.E(cr).elementMap<Any>().toList())

        readRecipe(cr)

        assertNotNull(cr)

        g.V().drop().iterate()

    }
}