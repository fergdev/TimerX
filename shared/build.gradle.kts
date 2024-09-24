@file:Suppress("UnusedPrivateProperty")

import org.intellij.lang.annotations.Language
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidJunit5)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.ksp)
    // TODO add plugin back in when this is supported again
//    alias(libs.plugins.room)
    alias(libs.plugins.kotlinx.serialization)
}
// region buildconfig
@Language("Kotlin")
// language=kotlin
val BuildConfig = """
    package ${Config.namespace}

    internal object BuildFlags {
        const val VersionName = "${Config.versionName}"
    }
""".trimIndent()

val generateBuildConfig by tasks.registering(Sync::class) {
    from(resources.text.fromString(BuildConfig)) {
        rename { "BuildFlags.kt" }
        into(Config.namespace.replace(".", "/"))
    }
    // the target directory
    into(layout.buildDirectory.dir("generated/kotlin/src/commonMain"))
}
// endregion

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
            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }
    // TODO enable native
//    macosArm64()
//    macosX64()

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateBuildConfig.map { it.destinationDir })

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
                api(compose.runtime)
                api(libs.decompose)
                api(libs.decompose.compose)
                api(libs.essenty.lifecycle)
                api(libs.essenty.stateKeeper)
                api(libs.essenty.instanceKeeper)
                api(libs.essenty.backHandler)
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(libs.flowmvi.compose)
                implementation(libs.flowmvi.core)
                implementation(libs.koin)
                implementation(libs.koin.compose)
                implementation(libs.kotlin.immutable)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.reorderable)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.coroutines)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.observable)
                implementation(libs.material.kolor)
                implementation(libs.flowext)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.coroutines.test)
            }
        }

        val mobileMain = create("mobileMain") {
            kotlin.srcDir("src/mobileMain/kotlin")
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.sqlite.bundled)
            }
        }

        val nonMobileMain = create("nonMobileMain") {
            kotlin.srcDir("src/nonMobileMain/kotlin")
            dependsOn(commonMain)
        }

        val nonAndroidMain = create("nonAndroidMain") {
            kotlin.srcDir("src/nonAndroidMain/kotlin")
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependencies {
                api(libs.firebase.analytics)
                api(libs.koin.android)
                api(libs.play.services.ads)
                implementation(libs.androidx.activity.compose)
                api(libs.kotlin.coroutines.android)
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
            dependsOn(nonAndroidMain)
        }
        val desktopMain by getting {
            dependsOn(nonMobileMain)
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlin.coroutines.swing)
            }
        }
        val wasmJsMain by getting {
            dependsOn(nonMobileMain)
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(libs.essenty.stateKeeper)
            }
        }
//        val nativeMain by getting
//        val macosMain by getting
    }
}

android {
    namespace = Config.namespace
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

compose {
    resources {
        // TODO change this to com.timerx
        packageOfResClass = "timerx.shared.generated.resources"
        publicResClass = true
    }
    android { }
    web { }
    desktop {
        application {
            mainClass = "${Config.namespace}.MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Exe)
                packageName = Config.namespace
                packageVersion = Config.majorVersionName
                description = Config.appDescription
                vendor = Config.vendorName
                licenseFile = rootProject.rootDir.resolve(Config.licenseFile)
                val iconDir = rootProject.rootDir.resolve("docs").resolve("images")

                macOS {
                    packageName = Config.name
                    dockName = Config.name
                    setDockNameSameAsPackageName = false
                    bundleID = Config.namespace
                    appCategory = "public.app-category.developer-tools"
//                    iconFile = iconDir.resolve("icon_macos.icns")
                }
                windows {
                    dirChooser = true
                    menu = false
                    shortcut = true
                    perUserInstall = true
                    upgradeUuid = Config.appId
//                    iconFile = iconDir.resolve("favicon.ico")
                }
                linux {
                    debMaintainer = Config.supportEmail
                    appCategory = "Development"
//                    iconFile = iconDir.resolve("icon_512.png")
                }
            }
        }
    }
}

//    "-DmainClass=com.timerx.MainKt"
tasks.withType<JavaExec>().named { it == "desktopRun" }
    .configureEach { mainClass = "com.timerx.MainKt" }

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
    implementation(libs.androidx.animation.core.android)
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
