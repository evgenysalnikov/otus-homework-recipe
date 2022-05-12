package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiStep
import com.salnikoff.recipe.api.v1.models.RecipeApiStepBase
import com.salnikoff.recipe.api.v1.models.RecipeApiStepWithImage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Encoder

internal class RecipeApiStepSerializer<T : IRecipeApiStep>(private val serializer: KSerializer<T>) :
    KSerializer<T> by serializer {
    override fun serialize(encoder: Encoder, value: T) {
        val step = when (value) {
            is RecipeApiStepBase -> value.copy(stepType = "base")
            is RecipeApiStepWithImage -> value.copy(stepType = "withImage")
            else -> throw SerializationException(
                "Unknown class to serialize as ISep instance in StepSerializer"
            )
        }

        @Suppress("UNCHECKED_CAST")
        return serializer.serialize(encoder, step as T)
    }
}
