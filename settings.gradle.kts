enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        maven("https://digital.artifacts.nz.thenational.com/repository/digital-maven")
        maven("https://digital.artifacts.nz.thenational.com/repository/gradle-plugins")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://digital.artifacts.nz.thenational.com/repository/digital-maven")
        maven("https://digital.artifacts.nz.thenational.com/repository/gradle-plugins")
        google()
        mavenCentral()
    }
}

rootProject.name = "TimerX"
include(":androidApp")
include(":shared")