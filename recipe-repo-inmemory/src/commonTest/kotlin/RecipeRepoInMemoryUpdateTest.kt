package com.salnikoff.recipe.repo.inmemory

import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.test.RepoRecipeUpdateTest

class RecipeRepoInMemoryUpdateTest : RepoRecipeUpdateTest() {
    override val repo: IRecipeRepository = RecipeRepoInMemory(
        initObjects = initObjects, randomUuid = { newLock.asString() }
    )
}
