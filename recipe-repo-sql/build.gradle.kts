plugins {
    kotlin("jvm")
}

tasks {
    withType<Test> {
        environment("recipe.sql_drop_db", true)
        environment("recipe.sql_fast_migration", true)
    }
}

group = rootProject.group
version = rootProject.version

dependencies {
    val exposedVersion: String by project
    val postgresDriverVersion: String by project
    val testContainersVersion: String by project

    implementation(kotlin("stdlib"))

    implementation(project(":recipe-common"))

    implementation("org.postgresql:postgresql:$postgresDriverVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation(project(":recipe-repo-test"))
}