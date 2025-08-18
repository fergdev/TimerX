@file:Suppress("UnusedPrivateProperty")
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
//    id(libs.plugins.androidApplication.get().pluginId)
    id(libs.plugins.kotlinMultiplatform.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-receivers")
    }

    jvm {
        compilerOptions {
            jvmTarget.set(Config.jvmTarget)
            freeCompilerArgs.addAll(Config.jvmCompilerArgs)
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(Config.compilerArgs)
                    optIn.addAll(Config.optIns)
                    progressiveMode.set(true)
                }
            }
        }
    }
    sourceSets {
        @OptIn(ExperimentalComposeLibrary::class)
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotest)
                implementation(libs.kotest.assertions.core)
                implementation(compose.uiTest)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(compose.material3)
            }
        }
    }
}
