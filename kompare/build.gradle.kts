@file:Suppress("UnusedPrivateProperty")
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("java-library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
//        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        freeCompilerArgs.addAll("-Xcontext-receivers")
    }
//    applyDefaultHierarchyTemplate()

    jvm {
        withJava()
    }
//    jvm().compilations.all {
//        compileTaskProvider.configure {
//            compilerOptions {
// //                jvmTarget.set(Config.jvmTarget)
//                freeCompilerArgs.addAll(Config.jvmCompilerArgs)
//            }
//        }
//    }

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
