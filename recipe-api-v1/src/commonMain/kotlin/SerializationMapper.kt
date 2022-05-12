package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
val serializationMapper = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true

    serializersModule = SerializersModule {
        polymorphicDefaultSerializer(IRecipeApiRequest::class) { instance ->
            @Suppress("UNCHECKED_CAST")
            when (instance) {
                is RecipeApiRecipeCreateRequest -> RecipeApiRequestSerializers.create as SerializationStrategy<IRecipeApiRequest>
                is RecipeApiRecipeReadRequest -> RecipeApiRequestSerializers.read as SerializationStrategy<IRecipeApiRequest>
                is RecipeApiRecipeUpdateRequest -> RecipeApiRequestSerializers.update as SerializationStrategy<IRecipeApiRequest>
                is RecipeApiRecipeDeleteRequest -> RecipeApiRequestSerializers.delete as SerializationStrategy<IRecipeApiRequest>
                is RecipeApiRecipeSearchRequest -> RecipeApiRequestSerializers.search as SerializationStrategy<IRecipeApiRequest>
                else -> null
            }
        }

        polymorphicDefaultSerializer(IRecipeApiResponse::class) { instance ->
            @Suppress("UNCHECKED_CAST")
            when (instance) {
                is RecipeApiRecipeCreateResponse -> RecipeApiResponseSerializers.create as SerializationStrategy<IRecipeApiResponse>
                is RecipeApiRecipeReadResponse -> RecipeApiResponseSerializers.read as SerializationStrategy<IRecipeApiResponse>
                is RecipeApiRecipeUpdateResponse -> RecipeApiResponseSerializers.update as SerializationStrategy<IRecipeApiResponse>
                is RecipeApiRecipeDeleteResponse -> RecipeApiResponseSerializers.delete as SerializationStrategy<IRecipeApiResponse>
                is RecipeApiRecipeSearchResponse -> RecipeApiResponseSerializers.search as SerializationStrategy<IRecipeApiResponse>
                else -> null
            }
        }

        polymorphicDefaultSerializer(IRecipeApiStep::class) { instance ->
            @Suppress("UNCHECKED_CAST")
            when (instance) {
                is RecipeApiStepBase -> RecipeApiStepSerializers.stepBase as SerializationStrategy<IRecipeApiStep>
                is RecipeApiStepWithImage -> RecipeApiStepSerializers.stepWithImage as SerializationStrategy<IRecipeApiStep>
                else -> null
            }
        }

        polymorphicDefault(IRecipeApiStep::class) {
            RecipeApiStepRequestSerializer()
        }
    }
}
