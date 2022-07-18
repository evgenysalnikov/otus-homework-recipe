package com.salnikoff.recipe.backend.services

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.common.RecipeContext

class RecipeService {

    val processor = RecipeProcessor()

    suspend fun exec(context: RecipeContext) = processor.exec(context)

    suspend fun createRecipe(context: RecipeContext) = processor.exec(context)
    suspend fun readRecipe(context: RecipeContext) = processor.exec(context)
    suspend fun updateRecipe(context: RecipeContext) = processor.exec(context)
    suspend fun deleteRecipe(context: RecipeContext) = processor.exec(context)
    suspend fun searchRecipe(context: RecipeContext) = processor.exec(context)

}
