package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiResponse

fun recipeApiV1ResponseSerialize(response: IRecipeApiResponse): String =
    serializationMapper.encodeToString(RecipeApiRecipeResponseSerializer, response)

@Suppress("UNCHECKED_CAST")
fun <T : Any> apiV1ResponseDeserialize(json: String): T =
    serializationMapper.decodeFromString(RecipeApiRecipeResponseSerializer, json) as T
