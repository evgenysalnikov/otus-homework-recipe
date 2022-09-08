package com.salnikoff.recipe.repo.gremlin.test

import com.salnikoff.recipe.common.models.RecipeRequirement
import com.salnikoff.recipe.repo.gremlin.RecipeRequirementGremlinRepo
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.otherV
import kotlin.test.*

class MyTest {

    private val hosts = ArcadeDbContainer.container.host
    private val port = ArcadeDbContainer.container.getMappedPort(8182)
    private val enableSsl = false

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
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            enableSsl = false,
            initObjects = emptyList(),
            initRepo = { g -> g.V().has("requirement").drop().iterate() },
        )
    }

    @Test
    fun reqRepoTest() {
        val newReq = recipeRequirementRepo.createRecipeRequirement(RecipeRequirement("req1"))
        assertNotNull(newReq)

        val newReq2 = recipeRequirementRepo.createRecipeRequirement(RecipeRequirement("req1"))
        assertNotNull(newReq2)

        assertEquals(newReq, newReq2)

        val reqFromRepo = recipeRequirementRepo.readRecipeRequirement(newReq)
        assertNotNull(reqFromRepo)
        assertEquals("req1", reqFromRepo.asString())

        val fromSearch = recipeRequirementRepo.searchRecipeRequirement("req1")
        assertNotNull(fromSearch)

        val fromSearchFalse = recipeRequirementRepo.searchRecipeRequirement("req2")
        assertNull(fromSearchFalse)

        val newReq3 = recipeRequirementRepo.createRecipeRequirement(RecipeRequirement("req2"))
        assertNotNull(newReq3)

        val reqFromRepoBySearch = recipeRequirementRepo.readRecipeRequirement(fromSearch)
        assertNotNull(reqFromRepoBySearch)
        assertEquals("req1", reqFromRepoBySearch.asString())

        val deleteResult1 = recipeRequirementRepo.deleteRecipeRequirement(newReq)
        assertTrue(deleteResult1)

        val reqFromRepo2 = recipeRequirementRepo.readRecipeRequirement(newReq)
        assertNull(reqFromRepo2)

        val deleteResult2 = recipeRequirementRepo.deleteRecipeRequirement(newReq)
        assertFalse(deleteResult2)

        val fromSearch2 = recipeRequirementRepo.searchRecipeRequirement("req1")
        assertNull(fromSearch2)

        val fromSearchReq2 = recipeRequirementRepo.searchRecipeRequirement("req2")
        assertNotNull(fromSearchReq2)
        println(fromSearchReq2)

    }

    @Test
    fun createAndReadTest() {


//
//        val ivan = g.addV("person").property("name", "Ivan").next()
//        val petr = g.addV("person").property("name", "Petr").next()
//        val dima = g.addV("person").property("name", "Dima").next()
//        g.V(ivan).addE("knows").to(petr).property("weight", 0.45).iterate()
//        g.V(ivan).addE("knows").to(dima).property("weight", 0.75).iterate()
//
////        val ivanFromRepo = g.V(ivan).elementMap<Any?>().toList()
////        println(ivanFromRepo)
//
//        val ivanFromRepo = g.V().has("person", "name", "Ivan").next()
//        val pals = g.V().has("person", "name", "Ivan").out("knows")
//            .elementMap<Any>().toList().let { println(it) }


        val req1 = g.addV("requirement").property("name", "req1").next()
        val req2 = g.addV("requirement").property("name", "req2").next()
        val req3 = g.addV("requirement").property("name", "req3").next()
//        val req1 = recipeRequirementRepo.createRecipeRequirement(RecipeRequirement("req1"))
//        val req2 = recipeRequirementRepo.createRecipeRequirement(RecipeRequirement("req2"))
//        val req3 = recipeRequirementRepo.createRecipeRequirement(RecipeRequirement("req3"))

        val recipe1 = g.addV("recipe")
            .property("title", "recipe1")
            .property("description", "recipe1 description")
            .next()

        val recipe2 = g.addV("recipe")
            .property("title", "recipe2")
            .property("description", "recipe2 description")
            .next()

        g.V(recipe1)
            .addE("depends").from(req1)
            .property("weight", 1)
            .iterate()

//        g.V(recipe1)
//            .addE("depends").from(req2)
//            .property("weight", 1)
//            .iterate()

        g.V(recipe1)
//            .select<Vertex>(req2).`as`("req2")
//            .select<Vertex>("recipe1")
            .addE("depends").from(req2)
            .property("weight", 1)
            .iterate()

        g.V(recipe2)
//            .select<Vertex>(req1).`as`("req1")
            .addE("depends").from(req1)
            .property("weight", 1)
            .iterate()

//        g.V(recipe2)
//            .addE("depends").from(req3)
//            .property("weight", 1)
//            .iterate()
        g.V(recipe2)
//            .select<Vertex>(req3).`as`("req3")
            .addE("depends").from(req3)
            .property("weight", 1)
            .iterate()

        println(recipe1)

        val rq2v = g.V(req2).next()
        g.V(recipe2)
            .addE("depends").from(rq2v)
            .property("weight", 1)
            .iterate()

//        g.V(recipe1)
//            .addE("depends").to(req2).property("weight", 1)
//            .iterate()


        g.V(recipe1).elementMap<Any>().toList().let { println(it) }

        g.V(recipe1).`in`("depends").elementMap<Any>().toList().let { println(it) }

        g.V(req2).out("depends").elementMap<Any>().toList().let { println(it) }

        g.V(recipe2)
            .bothE()
            .hasLabel("depends")
            .where(otherV().`is`(req2))
            .drop()
            .iterate()

        g.V(req2).out("depends").elementMap<Any>().toList().let { println(it) }


    }

}