package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.api.v1.apiV1ResponseDeserialize
import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.api.v1.recipeApiV1RequestSerialize
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.ProducerRecord
import org.rnorth.ducttape.unreliables.Unreliables
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals


class IntegrationTest {
    private val log = KotlinLogging.logger {}

    @Test
    fun integrationTest() {

        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
        kafka.start()

        val config = AppKafkaConfig(kafkaHosts = listOf(kafka.bootstrapServers))
        val clientProducer = config.createKafkaProducer()
        val clientConsumer = config.createKafkaConsumer()

        val topicInput = config.kafkaTopicInV1
        val topicOutput = config.kafkaTopicOutV1

        val processor = KafkaProcessor(config = config)
        val controller = KafkaController(setOf(processor))

        val record = ProducerRecord(
            topicInput, UUID.randomUUID().toString(), recipeApiV1RequestSerialize(
                RecipeApiRecipeCreateRequest(
                    requestId = "1111-2222-3333-4119",
                    recipe = RecipeApiRecipeCreateObject(
                        title = "some recipe",
                        description = "some recipe description"
                    ),
                    debug = RecipeApiDebug(
                        mode = RecipeApiRequestDebugMode.STUB,
                        stub = RecipeApiRequestDebugStubs.SUCCESS
                    )
                )
            )
        )
        clientProducer.send(record).get()

        controller.start()

        clientConsumer.subscribe(listOf(topicOutput))

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS) {
            val records: ConsumerRecords<String, String> = clientConsumer.poll(Duration.ofMillis(100))

            if (records.isEmpty) {
                return@retryUntilTrue false
            }

            log.info { "Found records ${records.count()}" }

            val message = records.first()

            log.info { "receiving message from topic = ${message.topic()} with key = ${message.key()} and value = ${message.value()}" }

            log.info { message.value() }

            val result = apiV1ResponseDeserialize<RecipeApiRecipeCreateResponse>(message.value())
            assertEquals("1111-2222-3333-4119", result.requestId)

            return@retryUntilTrue true
        }

        controller.stop()
        kafka.stop()

    }
}
