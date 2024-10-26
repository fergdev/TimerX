@file:Suppress("UnusedPrivateProperty")

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.StrictMode
import dev.mokkery.gradle.mokkery
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
    alias(libs.plugins.aboutLibs)
    alias(libs.plugins.kover)
}

@Language("Kotlin")
// language=kotlin
val buildConfig = """
    package ${Config.namespace}

    internal object BuildFlags {
        const val versionName = "${Config.versionName}"
        const val privacyPolicyUrl = "${Config.privacyPolicyUrl}"
        const val supportEmail = "${Config.supportEmail}"
    }
""".trimIndent()

val generateBuildConfig by tasks.registering(Sync::class) {
    from(resources.text.fromString(buildConfig)) {
        rename { "BuildFlags.kt" }
        into(Config.namespace.replace(".", "/"))
    }
    // the target directory
    into(layout.buildDirectory.dir("generated/kotlin/src/commonMain"))
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
            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }

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
                api(libs.essenty.lifecycle.coroutines)
                api(libs.essenty.stateKeeper)
                api(libs.essenty.instanceKeeper)
                api(libs.essenty.backHandler)
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(libs.flowmvi.compose)
                implementation(libs.flowmvi.core)
                implementation(libs.flowmvi.essenty)
                implementation(libs.koin)
                implementation(libs.koin.compose)
                implementation(libs.kotlin.immutable)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.reorderable)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.coroutines)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.observable)
                implementation(libs.material.kolor)
                implementation(libs.flowext)
                implementation(libs.aboutLibs)
                implementation(libs.kermit)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.kotest)
                implementation(libs.flowmvi.test)
                implementation(libs.turbine)
                implementation(libs.multiplatform.settings.test)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.kotest.junit)
                implementation(mokkery("coroutines"))
            }
        }

        val roomMain = create("roomMain") {
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
            dependsOn(roomMain)
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
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.junit.api)
                implementation(libs.junit.engine)
                implementation(libs.kotest.junit)
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
            dependsOn(roomMain)
            dependsOn(nonAndroidMain)
        }
        val desktopMain by getting {
            dependsOn(nonMobileMain)
            dependsOn(roomMain)
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlin.coroutines.swing)
                implementation(libs.korge.core)
                implementation(libs.freetts)
            }
        }
        val wasmJsMain by getting {
            dependsOn(nonMobileMain)
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(libs.kstore)
                implementation(libs.kstore.storage)
                implementation(libs.essenty.stateKeeper)
            }
        }
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
                val iconDir = rootProject.rootDir.resolve("playstore")

                macOS {
                    packageName = Config.appName
                    dockName = Config.appName
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
                    iconFile = iconDir.resolve("favicon.ico")
                }
                linux {
                    debMaintainer = Config.supportEmail
                    appCategory = "Development"
                    iconFile = iconDir.resolve("icon_512.png")
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
    testLogging {
        showExceptions = true
        showStandardStreams = true
    }
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
        "kspDesktop",
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

aboutLibraries {
    // - if the automatic registered android tasks are disabled, a similar thing can be achieved manually
    // - `./gradlew app:exportLibraryDefinitions -PaboutLibraries.exportPath=src/main/res/raw`
    // - the resulting file can for example be added as part of the SCM
    // registerAndroidTasks = false

    // define the path configuration files are located in. E.g. additional libraries, licenses to add to the target .json
    configPath = "config"
    // allow to enable "offline mode", will disable any network check of the plugin (including [fetchRemoteLicense] or pulling spdx license texts)
    offlineMode = false
    // enable fetching of "remote" licenses. Uses the GitHub API
    fetchRemoteLicense = true

    // Full license text for license IDs mentioned here will be included, even if no detected dependency uses them.
    // additionalLicenses = ["mit", "mpl_2_0"]

    // Allows to exclude some fields from the generated meta data field.
    // excludeFields = ["developers", "funding"]

    // Define the strict mode, will fail if the project uses licenses not allowed
    // - This will only automatically fail for Android projects which have `registerAndroidTasks` enabled
    // For non Android projects, execute `exportLibraryDefinitions`
    strictMode = StrictMode.FAIL
    // Allowed set of licenses, this project will be able to use without build failure
    allowedLicenses = arrayOf("Apache-2.0", "MIT", "BSD-3-Clause", "ASDKL", "NOASSERTION")
    // Allowed set of licenses for specific dependencies, this project will be able to use without build failure
    allowedLicensesMap = mapOf(Pair("asdkl", listOf("androidx.jetpack.library")))
    // Enable the duplication mode, allows to merge, or link dependencies which relate
    duplicationMode = DuplicateMode.LINK
    // Configure the duplication rule, to match "duplicates" with
    duplicationRule = DuplicateRule.SIMPLE
    // Enable pretty printing for the generated JSON file
    prettyPrint = true
}

kover {
    reports {
        // filters for all report types of all build variants
        filters {
            excludes {
                androidGeneratedClasses()
                packages("timerx.shared.generated.resources")
                classes("*\$special\$\$inlined\$map*")
            }
        }
    }
}
