package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.api.v1.apiV1ResponseDeserialize
import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.api.v1.recipeApiV1RequestSerialize
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class KafkaControllerTest {

    @Test
    fun runKafka() {
        val consumer = MockConsumer<String, String>(OffsetResetStrategy.EARLIEST)
        val producer = MockProducer<String, String>(true, StringSerializer(), StringSerializer())
        val config = AppKafkaConfig()
        val inputTopic = config.kafkaTopicInV1
        val outputTopic = config.kafkaTopicOutV1

        val app = AppKafkaConsumer(config, listOf(ConsumerStrategyV1()), consumer = consumer, producer = producer)
        consumer.schedulePollTask {
            consumer.rebalance(Collections.singletonList(TopicPartition(inputTopic, 0)))
            consumer.addRecord(
                ConsumerRecord(
                    inputTopic,
                    PARTITION,
                    0L,
                    "test-1",
                    recipeApiV1RequestSerialize(RecipeApiRecipeCreateRequest(
                        requestId = "1111-2222-3333-4444",
                        recipe = RecipeApiRecipeCreateObject(
                            title = "some recipe",
                            description = "some recipe description"
                        ),
                        debug = RecipeApiDebug(
                            mode = RecipeApiRequestDebugMode.STUB,
                            stub = RecipeApiRequestDebugStubs.SUCCESS
                        )
                    ))
                )
            )
            app.stop()
        }

        val startOffsets: MutableMap<TopicPartition, Long> = mutableMapOf()
        val tp = TopicPartition(inputTopic, PARTITION)
        startOffsets[tp] = 0L
        consumer.updateBeginningOffsets(startOffsets)

        app.run()

        val message = producer.history().first()

        val result = apiV1ResponseDeserialize<RecipeApiRecipeCreateResponse>(message.value())
        assertEquals(outputTopic, message.topic())
        assertEquals("1111-2222-3333-4444", result.requestId)
        assertEquals("Pie", result.recipe?.title)
        assertEquals("success create", result.recipe?.steps)
    }

    companion object {
        const val PARTITION = 0
    }
}
