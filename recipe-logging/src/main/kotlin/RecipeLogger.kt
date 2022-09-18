package com.salnikoff.recipe.logging

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

suspend fun <T> Logger.wrapWithLogging(
    id: String = "",
    block: suspend () -> T,
): T = try {
    val timeStart = Instant.now()
    info("Entering $id")
    block().also {
        val diffTime = Instant.now().toEpochMilli() - timeStart.toEpochMilli()
        info("Finishing $id", Pair("metricHandleTime", diffTime))
    }
} catch (e: Throwable) {
    error("Failing $id", e)
    throw e
}

fun recipeLogger(loggerId: String): RecipeLogWrapper = RecipeLogWrapper(
    logger = LoggerFactory.getLogger(loggerId) as Logger
)

fun recipeLogger(cls: Class<out Any>): RecipeLogWrapper = RecipeLogWrapper(
    logger = LoggerFactory.getLogger(cls) as Logger
)

/**
 * Generate internal RecipeLogContext logger
 *
 * @param logger Logback instance from [LoggerFactory.getLogger()]
 */
fun recipeLogger(logger: Logger): RecipeLogWrapper = RecipeLogWrapper(
    logger = logger,
    loggerId = logger.name,
)
