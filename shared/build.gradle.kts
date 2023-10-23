plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
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
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(libs.koin)
                implementation(libs.kotlin.immutable)
                implementation(libs.sql.delight)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sql.delight.android)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sql.delight.native)
            }
        }
    }
}

android {
    namespace = "com.timerx"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

sqldelight {
    database("AppDatabase") {
        packageName = "sqldelight"
    }
}