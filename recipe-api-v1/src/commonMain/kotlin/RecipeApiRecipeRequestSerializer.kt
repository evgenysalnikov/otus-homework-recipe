package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiRequest
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object RecipeApiRecipeRequestSerializer :
    JsonContentPolymorphicSerializer<IRecipeApiRequest>(IRecipeApiRequest::class) {
    private const val discriminator = "requestType"
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out IRecipeApiRequest> {
        return when (val discriminatorValue = element.jsonObject[discriminator]?.jsonPrimitive?.content) {
            "create" -> RecipeApiRequestSerializers.create
            "read" -> RecipeApiRequestSerializers.read
            "update" -> RecipeApiRequestSerializers.update
            "delete" -> RecipeApiRequestSerializers.delete
            "search" -> RecipeApiRequestSerializers.search
            else -> throw SerializationException(
                "Unknown value '${discriminatorValue}' in discriminator '$discriminator' " +
                        "property of IRequest implementation"
            )
        }
    }
}
