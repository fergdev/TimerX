import nl.littlerobots.vcu.plugin.versionSelector

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.crashlytics) apply false
}

versionCatalogUpdate {
    sortByKey = true

    versionSelector {
        stabilityLevel(it.candidate.version) >= minStabilityLevel
    }

    keep {
        keepUnusedVersions = true
        keepUnusedLibraries = true
        keepUnusedPlugins = true
    }
}

val stabilityLevels = listOf("snapshot", "eap", "preview", "alpha", "beta", "m", "cr", "rc")
val minStabilityLevel = stabilityLevels.indexOf("beta")
fun stabilityLevel(version: String): Int {
    stabilityLevels.forEachIndexed { index, postfix ->
        val regex = """.*[.\-]$postfix[.\-\d]*""".toRegex(RegexOption.IGNORE_CASE)
        if (version.matches(regex)) return index
    }
    return stabilityLevels.size
}

dependencies {
    detektPlugins(rootProject.libs.detekt.formatting)
    detektPlugins(rootProject.libs.detekt.compose)
    detektPlugins(rootProject.libs.detekt.libraries)
}

tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        buildUponDefaultConfig = true
        parallel = true
        setSource(projectDir)
//        config.setFrom(File(rootDir, Config.Detekt.configFile))
        config.setFrom(File(rootDir, "detekt.yml"))
        basePath = projectDir.absolutePath
//        jvmTarget = Config.jvmTarget.target
//        include(Config.Detekt.includedFiles)
//        exclude(Config.Detekt.excludedFiles)
        include("**/*.kt", "**/*.kts")
        exclude("**/resources/**", "**/build/**", "**/.idea/**")
        reports {
            xml.required.set(false)
            html.required.set(true)
            txt.required.set(false)
            sarif.required.set(true)
            md.required.set(false)
        }
    }

    register<io.gitlab.arturbosch.detekt.Detekt>("detektFormat") {
        description = "Formats whole project."
        autoCorrect = true
    }

    register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
        description = "Run detekt on whole project"
        autoCorrect = false
    }
}
