package com.salnikoff.recipe.repo.sql.test

import com.salnikoff.recipe.common.models.RecipeLock
import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.test.*

class RecipeRepoSqlCreateTest: RepoRecipeCreateTest() {
    override val repo: IRecipeRepository = SqlTestCompanion.repoUnderTestContainer(initObjects)
}

class RecipeRepoSQLDeleteTest : RepoRecipeDeleteTest() {
    override val repo: IRecipeRepository = SqlTestCompanion.repoUnderTestContainer(initObjects)
}

class RecipeRepoSQLReadTest : RepoRecipeReadTest() {
    override val repo: IRecipeRepository = SqlTestCompanion.repoUnderTestContainer(initObjects)
}

class RecipeRepoSQLSearchTest : RepoRecipeSearchTest() {
    override val repo: IRecipeRepository = SqlTestCompanion.repoUnderTestContainer(initObjects)
}

class RepoAdSQLUpdateTest : RepoRecipeUpdateTest() {
    override val newLock: RecipeLock = RecipeLock("id=30000-update-00000-1000")
    override val repo: IRecipeRepository = SqlTestCompanion.repoUnderTestContainer(initObjects, newLock)
}