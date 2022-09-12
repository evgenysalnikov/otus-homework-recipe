package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiRequest
import com.salnikoff.recipe.api.v1.models.IRecipeApiResponse
import com.salnikoff.recipe.api.v1.recipeApiV1RequestDeserialize
import com.salnikoff.recipe.api.v1.recipeApiV1ResponseSerialize
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeCommand
import com.salnikoff.recipe.common.models.RecipePrincipalModel
import com.salnikoff.recipe.common.models.asRecipeError
import com.salnikoff.recipe.logging.RecipeLogWrapper
import com.salnikoff.recipe.logs.mapper.toLog
import com.salnikoff.recipe.logs.mapper.toRecipeLog
import com.salnikoff.recipe.mappers.v1.fromTransport
import com.salnikoff.recipe.mappers.v1.toTransport
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock

suspend inline fun <reified Q : IRecipeApiRequest, reified R : IRecipeApiResponse> ApplicationCall.controllerHelperV1(
    logger: RecipeLogWrapper,
    logId: String,
    command: RecipeCommand,
    principal: RecipePrincipalModel,
    crossinline block: suspend RecipeContext.() -> Unit
) {
    val ctx = RecipeContext(
        timeStart = Clock.System.now(),
        principal = principal
    )

    try {
        logger.doWithLogging(logId) {
            val request = receiveText()
            ctx.fromTransport(recipeApiV1RequestDeserialize<Q>(request))
            logger.info(
                msg = "$command request is got",
                data = ctx.toLog("${logId}-got")
            )
            ctx.block()
            logger.info(
                msg = "$command request is handled",
                data = ctx.toLog("${logId}-handled")
            )
            val response = ctx.toTransport()
            respondText(recipeApiV1ResponseSerialize(response), ContentType.Application.Json)
        }
    } catch (e: Throwable) {
        command.also { ctx.command = it }
        ctx.state = CorState.FAILING
        ctx.errors.add(e.asRecipeError())
        logger.error(
            msg = "Fail to handle $command request",
            e = e,
            data = ctx.toLog("${logId}-error")
        )
        ctx.block()
        val response = ctx.toTransport()
        respondText(recipeApiV1ResponseSerialize(response), ContentType.Application.Json)
    }

}
