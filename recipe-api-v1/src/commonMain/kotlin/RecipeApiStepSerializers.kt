package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.RecipeApiStepBase
import com.salnikoff.recipe.api.v1.models.RecipeApiStepWithImage

internal object RecipeApiStepSerializers {
    val stepBase = RecipeApiStepSerializer(RecipeApiStepBase.serializer())
    val stepWithImage = RecipeApiStepSerializer(RecipeApiStepWithImage.serializer())
}
