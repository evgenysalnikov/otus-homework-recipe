package com.salnikoff.recipe.app.ktor

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.salnikoff.recipe.app.ktor.api.v1
import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig
import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig.Companion.GROUPS_CLAIM
import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipeSettings
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import com.salnikoff.recipe.repo.sql.RecipeRepoSQL
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


fun Application.getKtorAuthConfig() : KtorAuthConfig = KtorAuthConfig(
    secret = environment.config.property("jwt.secret").getString(),
    issuer = environment.config.property("jwt.issuer").getString(),
    audience = environment.config.property("jwt.audience").getString(),
    realm = environment.config.property("jwt.realm").getString()
)

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.module(
    settings: RecipeSettings? = null,
    authConfig: KtorAuthConfig = getKtorAuthConfig()
) {
    install(Routing)
    install(CachingHeaders)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(WebSockets)

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("RecipeCustomHeader")
        allowCredentials = true
        anyHost() // TODO remove
    }

    install(ContentNegotiation) {
        jackson {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(SerializationFeature.INDENT_OUTPUT)
            writerWithDefaultPrettyPrinter()
        }
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(Locations)

    val corSettings by lazy {
        settings ?: RecipeSettings(
            repoTest = RecipeRepoInMemory(),
            repoProd = RecipeRepoSQL(url = "jdbc:postgresql://sql:5432/recipedb")
//            repoProd = RecipeRepoSQL(url = "jdbc:postgresql://localhost:5432/recipedb")
        )
    }

    val recipeService = RecipeService(corSettings)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = authConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(authConfig.secret))
                    .withAudience(authConfig.audience)
                    .withIssuer(authConfig.issuer)
                    .build()
            )
            validate { jwtCredential: JWTCredential ->
                when {
                    jwtCredential.payload.getClaim(GROUPS_CLAIM).asList(String::class.java).isNullOrEmpty() -> {
                        this@module.log.error("Groups claim must not be empty in JWT token")
                        null
                    }
                    else -> JWTPrincipal(jwtCredential.payload)
                }
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Hello, world!")
        }

        v1(recipeService)

        static("static") {
            resources("static")
        }
    }
}
