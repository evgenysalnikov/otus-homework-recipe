package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Encoder

internal class RecipeApiResponseSerializer<T : IRecipeApiResponse>(private val serializer: KSerializer<T>) :
    KSerializer<T> by serializer {
    override fun serialize(encoder: Encoder, value: T) {
        val response = when (value) {
            is RecipeApiRecipeCreateResponse -> value.copy(responseType = "create")
            is RecipeApiRecipeReadResponse -> value.copy(responseType = "read")
            is RecipeApiRecipeUpdateResponse -> value.copy(responseType = "update")
            is RecipeApiRecipeDeleteResponse -> value.copy(responseType = "delete")
            is RecipeApiRecipeSearchResponse -> value.copy(responseType = "search")
            else -> throw SerializationException(
                "Unknown class to serialize as IResponse instance in RequestSerializer"
            )
        }

        @Suppress("UNCHECKED_CAST")
        return serializer.serialize(encoder, response as T)
    }
}
