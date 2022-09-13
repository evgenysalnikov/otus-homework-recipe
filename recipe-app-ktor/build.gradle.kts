import org.jetbrains.kotlin.util.suffixIfNot

val ktor_version: String by project

// ex: Converts to "io.ktor:ktor-ktor-server-netty:2.0.1" with only ktor("netty")
fun DependencyHandler.ktor(module: String, prefix: String = "server-", version: String? = ktor_version): Any =
    "io.ktor:ktor-${prefix.suffixIfNot("-")}$module:$version"

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("application")
    id("com.bmuschko.docker-java-application")
}

group = rootProject.group
version = rootProject.version

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

docker {
    javaApplication {
        mainClassName.set(application.mainClass.get())
//        baseImage.set("adoptopenjdk/openjdk17:alpine-jre")
        baseImage.set("openjdk:17-alpine3.14")
        maintainer.set("evgeny@salnikoff.com")
        ports.set(listOf(8080))
        val imageName = project.name
        images.set(
            listOf(
                "$imageName:${project.version}",
                "$imageName:latest"
            )
        )
        jvmArgs.set(listOf("-Xms256m", "-Xmx512m"))
    }
}


dependencies {
    val logback_version: String by project
    val kotlinVersion: String by project
    val kotlinLoggingJvmVersion: String by project

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(ktor("core")) // "io.ktor:ktor-server-core:$ktorVersion"
    implementation(ktor("netty")) // "io.ktor:ktor-ktor-server-netty:$ktorVersion"

    // jackson
    implementation(ktor("jackson", "serialization")) // io.ktor:ktor-serialization-jackson
    implementation(ktor("content-negotiation")) // io.ktor:ktor-server-content-negotiation

    implementation(ktor("locations"))
    implementation(ktor("caching-headers"))
    implementation(ktor("call-logging"))
    implementation(ktor("auto-head-response"))
    implementation(ktor("cors")) // "io.ktor:ktor-cors:$ktorVersion"
    implementation(ktor("default-headers")) // "io.ktor:ktor-cors:$ktorVersion"
    implementation(ktor("auto-head-response"))
    implementation(ktor("websockets"))
    implementation(ktor("auth"))
    implementation(ktor("auth-jwt"))

    implementation(ktor("websockets")) // "io.ktor:ktor-websockets:$ktorVersion"
    // implementation(ktor("auth")) // "io.ktor:ktor-auth:$ktorVersion"
    // implementation(ktor("auth-jwt")) // "io.ktor:ktor-auth-jwt:$ktorVersion"

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingJvmVersion")


    implementation(project(":recipe-api-v1"))
    implementation(project(":recipe-common"))
    implementation(project(":recipe-mappers-v1"))
    implementation(project(":recipe-services"))
    implementation(project(":recipe-stubs"))
    implementation(project(":recipe-repo-inmemory"))
    implementation(project(":recipe-repo-gremlin"))
    implementation(project(":recipe-repo-sql"))

    testImplementation(kotlin("test-junit"))
    testImplementation(ktor("test-host")) // "io.ktor:ktor-server-test-host:$ktorVersion"
    testImplementation(ktor("content-negotiation", prefix = "client-"))
    testImplementation(ktor("websockets", prefix = "client-"))

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
