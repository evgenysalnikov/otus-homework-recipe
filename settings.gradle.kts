rootProject.name = "otus-homework-recipe"

pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val kotestVersion: String by settings
        val openapiVersion: String by settings

        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatfrom") version kotlinVersion apply false
        id("io.kotest.multiplatform") version kotestVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false

        id("org.openapi.generator") version openapiVersion apply false
    }
}

include("recipe-api-v1")
include("recipe-common")
include("recipe-mappers-v1")
include("recipe-services")
include("recipe-stubs")
include("recipe-app-ktor")
