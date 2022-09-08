package com.salnikoff.recipe.repo.gremlin.test

import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.repo.gremlin.RecipeRepoGremlin
import com.salnikoff.recipe.repo.test.RepoRecipeReadTest

class RecipeRepoGremlinReadTest: RepoRecipeReadTest() {
    override val repo: RecipeRepoGremlin by lazy {
        RecipeRepoGremlin(
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            enableSsl = false,
            initObjects = initObjects,
            initRepo = { g -> g.V().drop().iterate() },
        )
    }

    override val successId: RecipeId by lazy {
        repo.initializedObjects.firstOrNull()?.id ?: RecipeId.NONE
    }
}