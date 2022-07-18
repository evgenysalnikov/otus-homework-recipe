plugins {
    kotlin("multiplatform")
}

group = rootProject.group
version = rootProject.version


kotlin {
    jvm {}

    sourceSets {
        val coroutinesVersion: String by project
        val kotlinCorVersion: String by project

        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":recipe-common"))
                implementation(project(":recipe-stubs"))
                implementation(project(":chain-of-responsibility"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(project(":recipe-repo-inmemory"))
            }
        }
    }
}
