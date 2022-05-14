package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Encoder

internal class RecipeApiRequestSerializer<T : IRecipeApiRequest>(private val serializer: KSerializer<T>) :
    KSerializer<T> by serializer {
    override fun serialize(encoder: Encoder, value: T) {
        val request = when (value) {
            is RecipeApiRecipeCreateRequest -> value.copy(requestType = "create")
            is RecipeApiRecipeReadRequest -> value.copy(requestType = "read")
            is RecipeApiRecipeUpdateRequest -> value.copy(requestType = "update")
            is RecipeApiRecipeDeleteRequest -> value.copy(requestType = "delete")
            is RecipeApiRecipeSearchRequest -> value.copy(requestType = "search")
            else -> throw SerializationException(
                "Unknown class to serialize as IRequest instance in RequestSerializer"
            )
        }

        @Suppress("UNCHECKED_CAST")
        return serializer.serialize(encoder, request as T)
    }
}
