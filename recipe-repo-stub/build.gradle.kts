plugins {
    kotlin("multiplatform")
}

group = rootProject.group
version = rootProject.version


kotlin {
    jvm {}

    sourceSets {
        val coroutinesVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation(project(":recipe-common"))
                implementation(project(":recipe-stubs"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(project(":recipe-repo-test"))
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
            }
        }
    }
}