@file:Suppress(
    "MissingPackageDeclaration",
    "MemberVisibilityCanBePrivate",
)

import org.gradle.api.JavaVersion

object Config {
    //    val jvmTarget = JvmTarget.JVM_11
//    val idePluginJvmTarget = JvmTarget.JVM_17
    val javaVersion = JavaVersion.VERSION_17
    const val compileSdk = 34
    const val targetSdk = compileSdk
    const val minSdk = 21

    val stabilityLevels = listOf("snapshot", "eap", "preview", "alpha", "beta", "m", "cr", "rc")
    val minStabilityLevel = stabilityLevels.indexOf("beta")

    val optIns = listOf(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
        "kotlin.RequiresOptIn",
        "kotlin.experimental.ExperimentalTypeInference",
        "kotlin.contracts.ExperimentalContracts",
        "org.jetbrains.compose.resources.ExperimentalResourceApi"
    )
    val compilerArgs = listOf(
        "-Xbackend-threads=0", // parallel IR compilation
        "-Xexpect-actual-classes",
        "-Xconsistent-data-class-copy-visibility"
    )
    val jvmCompilerArgs = buildList {
        addAll(compilerArgs)
        add("-Xjvm-default=all") // enable all jvm optimizations
        add("-Xcontext-receivers")
        add("-Xstring-concat=inline")
        add("-Xlambdas=indy")
    }

    object Detekt {
        const val CONFIG_FILE = "detekt.yml"
        val includedFiles = listOf("**/*.kt", "**/*.kts")
        val excludedFiles = listOf("**/resources/**", "**/build/**", "**/.idea/**")
    }
}
