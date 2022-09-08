package com.salnikoff.recipe.repo.gremlin.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.repo.gremlin.RecipeRepoGremlin
import com.salnikoff.recipe.repo.test.RepoRecipeSearchTest

class RecipeRepoGremlinSearchTest: RepoRecipeSearchTest() {
    override val repo: RecipeRepoGremlin by lazy {
        RecipeRepoGremlin(
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            enableSsl = false,
            initObjects = initObjects,
            initRepo = { g -> g.V().drop().iterate() },
        )
    }
    override val initRecipes: List<Recipe> = repo.initializedObjects
}