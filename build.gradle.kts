
plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.detekt)
    alias(libs.plugins.androidApplication) apply (false)
    alias(libs.plugins.androidLibrary) apply (false)
    alias(libs.plugins.kotlinAndroid) apply (false)
    alias(libs.plugins.kotlinMultiplatform) apply (false)
    alias(libs.plugins.jetbrainsCompose) apply (false)
    alias(libs.plugins.composeCompiler) apply (false)
    alias(libs.plugins.googleServices) apply (false)
    alias(libs.plugins.crashlytics) apply (false)
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
