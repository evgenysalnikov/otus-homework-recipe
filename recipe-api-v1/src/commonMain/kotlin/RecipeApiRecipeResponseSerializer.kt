package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object RecipeApiRecipeResponseSerializer :
    JsonContentPolymorphicSerializer<IRecipeApiResponse>(IRecipeApiResponse::class) {
    private const val discriminator = "responseType"
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out IRecipeApiResponse> {
        return when (val discriminatorValue = element.jsonObject[discriminator]?.jsonPrimitive?.content) {
            "create" -> RecipeApiResponseSerializers.create
            "read" -> RecipeApiResponseSerializers.read
            "update" -> RecipeApiResponseSerializers.update
            "delete" -> RecipeApiResponseSerializers.delete
            "search" -> RecipeApiResponseSerializers.search
            else -> throw SerializationException(
                "Unknown value '${discriminatorValue}' in discriminator '$discriminator' " +
                        "property of IResponse implementation"
            )
        }
    }
}
