package com.salnikoff.recipe.kafka

import kotlinx.coroutines.*
import java.util.concurrent.Executors

class KafkaController(private val processors: Set<KafkaProcessor>) {
    private val scope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName("kafka-controller")
    )

    fun start() = scope.launch {
        processors.forEach { processor ->
            launch(
                Executors.newSingleThreadExecutor()
                    .asCoroutineDispatcher() + CoroutineName("kafka-process-${processor.config.kafkaGroupId}")
            ) {
                try {
                    processor.process()
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }

    fun stop() = run {
        processors.forEach { processor ->
            processor.stop()
        }
//        scope.cancel("controller was stopped")
    }
}
