package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.IRecipeApiRequest
import com.salnikoff.recipe.api.v1.models.IRecipeApiResponse
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeCommand
import com.salnikoff.recipe.common.models.asRecipeError
import com.salnikoff.recipe.mappers.v1.fromTransport
import com.salnikoff.recipe.mappers.v1.toTransport
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock

suspend inline fun <reified Q : IRecipeApiRequest, reified R : IRecipeApiResponse> ApplicationCall.controllerHelperV1(
    command: RecipeCommand,
    block: RecipeContext.() -> Unit
) {
    val ctx = RecipeContext(
        timeStart = Clock.System.now()
    )

    try {
        val request = receive<Q>()
        ctx.fromTransport(request)
        ctx.block()
        val response = ctx.toTransport()
        respond(response)
    } catch (e: Throwable) {
        command?.also { ctx.command = it }
        ctx.state = CorState.FAILING
        ctx.errors.add(e.asRecipeError())
        ctx.block()
        val response = ctx.toTransport()
        respond(response)
    }

}
