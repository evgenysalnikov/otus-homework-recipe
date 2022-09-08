package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipeSettings
import com.salnikoff.recipe.repo.gremlin.RecipeRepoGremlin
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory

fun main(settings: RecipeSettings? = null) {
    val config = AppKafkaConfig()

    val corSettings by lazy {
        settings ?: RecipeSettings(
            repoTest = RecipeRepoInMemory(),
            repoProd = RecipeRepoGremlin(
                "localhost",
                2424
            ),
        )
    }
    val service = RecipeService(corSettings)

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
