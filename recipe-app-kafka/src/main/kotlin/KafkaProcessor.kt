package com.salnikoff.recipe.kafka

import com.salnikoff.recipe.api.v1.models.IRecipeApiRequest
import com.salnikoff.recipe.api.v1.models.IRecipeApiResponse
import com.salnikoff.recipe.api.v1.recipeApiV1RequestDeserialize
import com.salnikoff.recipe.api.v1.recipeApiV1ResponseSerialize
import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.RecipeSettings
import com.salnikoff.recipe.mappers.v1.fromTransport
import com.salnikoff.recipe.mappers.v1.toTransport
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.WakeupException
import java.time.Duration
import java.util.*

private val log = KotlinLogging.logger {}

class KafkaProcessor(
    val config: AppKafkaConfig,
    private val service: RecipeService = RecipeService(RecipeSettings()),
    private val consumer: Consumer<String, String> = config.createKafkaConsumer(),
    private val producer: Producer<String, String> = config.createKafkaProducer()
) {
    private val isRunning = atomic(true)

    fun process() = runBlocking {
        try {
            consumer.subscribe(listOf(config.kafkaTopicInV1))

            val ctx = RecipeContext(
                timeStart = Clock.System.now()
            )

            while (isRunning.value) {

                val records: ConsumerRecords<String, String> = withContext(Dispatchers.IO) {
                    consumer.poll(Duration.ofSeconds(1))
                }
                if (!records.isEmpty)
                    log.info { "Receive ${records.count()} messages" }

                records.forEach { record: ConsumerRecord<String, String> ->
                    try {
                        log.info { "process ${record.key()} from ${record.topic()}:\n${record.value()}" }

                        val request: IRecipeApiRequest = recipeApiV1RequestDeserialize(record.value())
                        ctx.fromTransport(request)

                        service.exec(ctx)
                        sendResponse(ctx)

                    } catch (ex: Throwable) {
                        log.error(ex) { "error" }
                    }
                }

            }
        } catch (ex: WakeupException) {
            // ignore for shutdown
        } catch (ex: RuntimeException) {
            // exception handling
            withContext(NonCancellable) {
                throw ex
            }
        } finally {
            withContext(NonCancellable) {
                consumer.close()
            }
        }
    }

    private fun sendResponse(ctx: RecipeContext) {
        val response: IRecipeApiResponse = ctx.toTransport()
        val json = recipeApiV1ResponseSerialize(response)
        val resRecord = ProducerRecord(config.kafkaTopicOutV1, UUID.randomUUID().toString(), json)

        log.info { "sending ${resRecord.key()} to ${config.kafkaTopicOutV1}:\n$json" }

        producer.send(resRecord)
    }

    fun stop() {
        isRunning.value = false
    }
}
