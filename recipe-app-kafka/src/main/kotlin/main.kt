package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.backend.services.RecipeService

fun main() {
    val config = AppKafkaConfig()

    val service = RecipeService()

    val processor by lazy {
        KafkaProcessor(
            config = config,
            service = service
        )
    }

    val controller by lazy {
        KafkaController(processors = setOf(processor))
    }

    controller.start()
}
