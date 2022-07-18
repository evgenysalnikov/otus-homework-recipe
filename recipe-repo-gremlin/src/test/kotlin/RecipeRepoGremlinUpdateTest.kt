package com.salnikoff.recipe.repo.gremlin.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.repo.gremlin.RecipeRepoGremlin
import com.salnikoff.recipe.repo.test.RepoRecipeUpdateTest

class RecipeRepoGremlinUpdateTest: RepoRecipeUpdateTest() {
    override val repo: RecipeRepoGremlin by lazy {
        RecipeRepoGremlin(
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            enableSsl = false,
            initObjects = initObjects,
            initRepo = { g -> g.V().drop().iterate() },
            randomUuid = { newLock.asString() },
        )
    }
    override val updateId: RecipeId = repo.initializedObjects.first().id
    override val updateObj: Recipe = super.updateObj.copy(id = updateId)
}