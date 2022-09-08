package com.salnikoff.recipe.repo.gremlin.test

import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.gremlin.RecipeRepoGremlin
import com.salnikoff.recipe.repo.test.RepoRecipeCreateTest
import com.salnikoff.recipe.repo.test.RepoRecipeSearchTest

class RecipeRepoGremlinCreateTest : RepoRecipeCreateTest() {
    override val repo: IRecipeRepository by lazy {
        RecipeRepoGremlin(
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            enableSsl = false,
            initObjects = RepoRecipeSearchTest.initObjects,
            initRepo = { g -> g.V().drop().iterate() },
        )
    }
}