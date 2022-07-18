package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.api.v1.models.IRecipeApiRequest
import com.salnikoff.recipe.api.v1.models.IRecipeApiResponse
import com.salnikoff.recipe.api.v1.recipeApiV1RequestDeserialize
import com.salnikoff.recipe.api.v1.recipeApiV1ResponseSerialize
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.mappers.v1.fromTransport
import com.salnikoff.recipe.mappers.v1.toTransport

class ConsumerStrategyV1 : ConsumerStrategy {
    override fun topics(config: AppKafkaConfig): InputOutputTopics {
        return InputOutputTopics(config.kafkaTopicInV1, config.kafkaTopicOutV1)
    }

    override fun serialize(source: RecipeContext): String {
        val response: IRecipeApiResponse = source.toTransport()
        return recipeApiV1ResponseSerialize(response)
    }

    override fun deserialize(value: String, target: RecipeContext) {
        val request: IRecipeApiRequest = recipeApiV1RequestDeserialize(value)
        target.fromTransport(request)
    }
}
