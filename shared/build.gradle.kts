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

    androidTarget()

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
                // Common compiler options applied to all Kotlin source sets
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }

            dependencies {
                implementation(libs.koin)
                api(libs.koin.compose)

                implementation(libs.kotlin.immutable)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)

                implementation(compose.components.resources)

                implementation(libs.pre.compose)
                implementation(libs.pre.compose.viewmodel)
                implementation(libs.pre.compose.koin)
                implementation(libs.kotlinx.datetime)
                implementation(libs.reorderable)
                implementation(libs.androidx.data.store.core)
                implementation(libs.androidx.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.kotlinx.serialization)
                // Core KMP module
                implementation("pro.respawn.flowmvi:core:3.0.0")
                // compose multiplatform
                implementation("pro.respawn.flowmvi:compose:3.0.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                api(libs.koin.android)
                api(libs.androidx.activity.compose)
                //noinspection BomWithoutPlatform
                implementation(libs.firebase.bom)
                api(libs.firebase.analytics)
                implementation(libs.firebase.crashlytics)
                api(libs.play.services.ads)
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
        val iosMain by getting
    }
}

android {
    namespace = "com.timerx"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
