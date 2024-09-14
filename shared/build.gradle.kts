@file:Suppress("UnusedPrivateProperty")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidJunit5)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.ksp)
    // TODO add plugin back in when this is supported again
//    alias(libs.plugins.room)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()


    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "app"
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "app.js"
                export = true
            }
            testTask { enabled = false }
        }
    }

    jvm("desktop")

    androidTarget().compilations.all {
        compileTaskProvider.configure {
            compilerOptions {
//                jvmTarget = Config.jvmTarget
                freeCompilerArgs.addAll(Config.jvmCompilerArgs)
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        val commonMain by getting {

            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions {
                languageVersion.set(KotlinVersion.KOTLIN_2_0)
                freeCompilerArgs.addAll(Config.compilerArgs)
            }
            all {
                languageSettings {
                    progressiveMode = true
                    Config.optIns.forEach { optIn(it) }
                }
            }

            dependencies {
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(libs.flowmvi.compose)
                implementation(libs.flowmvi.core)
                implementation(libs.koin)
                implementation(libs.koin.compose)
                implementation(libs.kotlin.immutable)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.pre.compose)
                implementation(libs.pre.compose.koin)
                implementation(libs.pre.compose.viewmodel)
                implementation(libs.reorderable)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val mobileMain = create("mobileMain") {
            kotlin.srcDir("src/mobileMain/kotlin")
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.data.store.core)
                implementation(libs.androidx.room.runtime)
                implementation(libs.sqlite.bundled)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependencies {
                api(libs.firebase.analytics)
                api(libs.koin.android)
                api(libs.play.services.ads)
                implementation(libs.androidx.activity.compose)
                //noinspection BomWithoutPlatform
                implementation(libs.firebase.bom)
                implementation(libs.firebase.crashlytics)
                implementation(libs.androidx.glance.appwidget)
                implementation(libs.androidx.glance.material3)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.androidx.core.splashscreen)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.junit.api)
                implementation(libs.junit.engine)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.junit.api)
                implementation(libs.junit.engine)
                implementation(libs.junit5.android.test.core)
                implementation(libs.junit5.android.test.runner)
                implementation(libs.junit5.android.test.compose)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(mobileMain)
        }
        val desktopMain by getting {}
        val wasmJsMain by getting
    }
}

android {
    namespace = "com.timerx"
    compileSdk = Config.compileSdk
    defaultConfig {
        minSdk = Config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }
    compileOptions {
        sourceCompatibility = Config.javaVersion
        targetCompatibility = Config.javaVersion
    }
    kotlin {
        jvmToolchain(17)
    }
}

junitPlatform {
    // Using local dependency instead of Maven coordinates
    instrumentationTests.enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// TODO enable room plugin when it works properly
// room {
//    schemaDirectory("$projectDir/schemas")
// }
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    listOf(
        "kspAndroid",
        "kspIosSimulatorArm64",
        "kspIosX64",
        "kspIosArm64"
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.file("shared/compose_stability_config.conf")
}
