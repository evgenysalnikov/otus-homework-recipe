package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*

internal object RecipeApiRequestSerializers {
    val create = RecipeApiRequestSerializer(RecipeApiRecipeCreateRequest.serializer())
    val read = RecipeApiRequestSerializer(RecipeApiRecipeReadRequest.serializer())
    val update = RecipeApiRequestSerializer(RecipeApiRecipeUpdateRequest.serializer())
    val delete = RecipeApiRequestSerializer(RecipeApiRecipeDeleteRequest.serializer())
    val search = RecipeApiRequestSerializer(RecipeApiRecipeSearchRequest.serializer())
}
