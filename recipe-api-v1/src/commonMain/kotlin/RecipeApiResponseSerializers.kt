package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*

internal object RecipeApiResponseSerializers {
    val create = RecipeApiResponseSerializer(RecipeApiRecipeCreateResponse.serializer())
    val read = RecipeApiResponseSerializer(RecipeApiRecipeReadResponse.serializer())
    val update = RecipeApiResponseSerializer(RecipeApiRecipeUpdateResponse.serializer())
    val delete = RecipeApiResponseSerializer(RecipeApiRecipeDeleteResponse.serializer())
    val search = RecipeApiResponseSerializer(RecipeApiRecipeSearchResponse.serializer())
}
