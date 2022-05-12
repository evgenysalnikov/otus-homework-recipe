package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiStep
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class RecipeApiStepRequestSerializer :
    JsonContentPolymorphicSerializer<IRecipeApiStep>(IRecipeApiStep::class) {
    private val discriminator = "stepType"
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out IRecipeApiStep> {

        return when (val discriminatorValue = element.jsonObject[discriminator]?.jsonPrimitive?.content) {
            "base" -> RecipeApiStepSerializers.stepBase
            "withImage" -> RecipeApiStepSerializers.stepWithImage
            else -> throw SerializationException(
                "Unknown value '${discriminatorValue}' in discriminator '$discriminator' " +
                        "property of IStep implementation"
            )
        }
    }
}
