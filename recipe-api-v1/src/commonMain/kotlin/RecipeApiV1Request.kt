package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiRequest

fun recipeApiV1RequestSerialize(request: IRecipeApiRequest): String =
    serializationMapper.encodeToString(RecipeApiRecipeRequestSerializer, request)

@Suppress("UNCHECKED_CAST")
fun <T : Any> recipeApiV1RequestDeserialize(json: String): T =
    serializationMapper.decodeFromString(RecipeApiRecipeRequestSerializer, json) as T
