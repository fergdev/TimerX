@file:Suppress(
    "MissingPackageDeclaration",
    "MemberVisibilityCanBePrivate",
)

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Config {
    const val appName = "Intervallum"
    const val namespace = "com.intervallum"
    const val namespaceAndroid = "$namespace.android"
    const val versionCode = 5
    const val majorRelease = 1
    const val minorRelease = 0
    const val patch = 4
    const val majorVersionName = "$majorRelease.$minorRelease.$patch"
    const val versionName = "$majorRelease.$minorRelease.$patch"

    const val storeFilePath = "certificates/intervallum.jks"

    @Suppress("MaxLineLength")
    const val appDescription =
        "Intervallum is a beautiful, fully customizable HIT timer designed to help you crush your fitness goals by keeping you focused and in control during every workout."
    const val vendorName = "Ferg.Dev"
    const val licenseFile = "LICENSE.txt"
    const val compileSdk = 36
    const val targetSdk = compileSdk
    const val minSdk = 23
    const val appId = "4a07189f-4143-4a90-9f53-1eedd74ddbeb"
    const val supportEmail = "ferg.dev@outlook.com"
    const val privacyPolicyUrl =
        "https://doc-hosting.flycricket.io/intervallum-privacy-policy/2fa3244e-cdef-4ec8-b776-6c03e94e8fab/privacy"

    val stabilityLevels = listOf("snapshot", "eap", "preview", "alpha", "beta", "m", "cr", "rc")
    val minStabilityLevel = stabilityLevels.indexOf("beta")

    val optIns = listOf(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
        "kotlin.RequiresOptIn",
        "kotlin.experimental.ExperimentalTypeInference",
        "kotlin.contracts.ExperimentalContracts",
        "org.jetbrains.compose.resources.ExperimentalResourceApi",
        "kotlin.time.ExperimentalTime"
    )
    val compilerArgs = listOf(
        "-Xexpect-actual-classes",
        "-Xcontext-receivers",
        "-Xconsistent-data-class-copy-visibility"
    )
    val jvmCompilerArgs = buildList {
        addAll(compilerArgs)
        add("-Xjvm-default=all") // enable all jvm optimizations
        add("-Xcontext-receivers")
        add("-Xstring-concat=inline")
        add("-Xlambdas=indy")
    }

    val jvmTarget = JvmTarget.JVM_21
    val javaVersion = JavaVersion.VERSION_21

    object Detekt {
        const val CONFIG_FILE = "detekt.yml"
        val includedFiles = listOf("**/*.kt", "**/*.kts")
        val excludedFiles = listOf("**/resources/**", "**/build/**", "**/.idea/**")
    }
}
