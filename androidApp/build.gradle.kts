@file:Suppress("UnusedPrivateProperty")

import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

android {
    namespace = Config.namespaceAndroid
    compileSdk = Config.compileSdk
    defaultConfig {
        applicationId = Config.namespaceAndroid
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.versionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file(Config.KeyStore.propertiesFile)
            val keystoreProperties = Properties()
            @Suppress("SwallowedException")
            try {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                storePassword = keystoreProperties.getProperty(Config.KeyStore.storePasswordKey)
                storeFile = File(keystoreProperties.getProperty(Config.KeyStore.storeFileKey))
                keyPassword = keystoreProperties.getProperty(Config.KeyStore.keyPasswordKey)
                keyAlias = keystoreProperties.getProperty(Config.KeyStore.aliasKey)
            } catch (e : IOException){
                // do nothing
            }
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // Includes a local, custom Proguard rules file
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
        }
    }
    compileOptions {
        sourceCompatibility = Config.javaVersion
        targetCompatibility = Config.javaVersion
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        jvmToolchain(17)
    }
}
