package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.api.v1.apiV1ResponseDeserialize
import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.api.v1.recipeApiV1RequestSerialize
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.ProducerRecord
import org.rnorth.ducttape.unreliables.Unreliables
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals


class IntegrationTest {
    private val log = KotlinLogging.logger {}

    private val kafka by lazy {
        KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .apply { start() }
    }
    private val config = AppKafkaConfig(kafkaHosts = listOf(kafka.bootstrapServers))

    private val clientProducer by lazy { config.createKafkaProducer() }
    private val clientConsumer by lazy { config.createKafkaConsumer() }

    private val topicInput by lazy { config.kafkaTopicInV1 }
    private val topicOutput by lazy { config.kafkaTopicOutV1 }

    private val scope = CoroutineScope(
        Executors.newSingleThreadExecutor()
            .asCoroutineDispatcher() + CoroutineName("thread-test-kafka")
    )

    @Test
    fun integrationTest() {

        val record = ProducerRecord(
            topicInput, UUID.randomUUID().toString(), recipeApiV1RequestSerialize(
                RecipeApiRecipeCreateRequest(
                    requestId = "1111-2222-3333-4118",
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

        scope.launch {
            val app = AppKafkaConsumer(config, listOf(ConsumerStrategyV1()))
            app.run()
        }

        clientConsumer.subscribe(listOf(topicOutput))

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS) {
            val records: ConsumerRecords<String, String> = clientConsumer.poll(Duration.ofMillis(100))

            if (records.isEmpty) {
                return@retryUntilTrue false
            }

            log.info { "Found records ${records.count()}" }

            val message = records.first()

            log.info { "[x] receiving message from topic = ${message.topic()} with key = ${message.key()} and value = ${message.value()}" }

            log.info { message.value() }

            val result = apiV1ResponseDeserialize<RecipeApiRecipeCreateResponse>(message.value())
            assertEquals("1111-2222-3333-4118", result.requestId)

            return@retryUntilTrue true
        }

        scope.cancel()

    }
}