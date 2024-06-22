
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.realm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidJunit5)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.googleServices)
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
        }
    }

    sourceSets {
        val commonMain by getting {
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
                implementation(libs.realm.library.base)
                implementation(libs.kotlinx.datetime)
                implementation(libs.reorderable)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
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
            dependencies { }
        }
    }
}

android {
    namespace = "com.timerx"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
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

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    source.setFrom(
        "src/androidMain/kotlin",
        "src/androidUnitTest/kotlin",
        "src/commonMain/kotlin",
        "src/commonTest/kotlin",
        "src/iosMain/kotlin",
        "src/iosTest/kotlin",
    )
    config.setFrom("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

//subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler"
                )
            }
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
                        "${rootDir}/shared/compose_compiler_config.conf"
            )
        }
    }
//}
