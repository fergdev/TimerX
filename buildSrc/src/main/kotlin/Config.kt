@file:Suppress(
    "MissingPackageDeclaration",
    "MemberVisibilityCanBePrivate",
)

import org.gradle.api.JavaVersion

object Config {
    //    val jvmTarget = JvmTarget.JVM_11
//    val idePluginJvmTarget = JvmTarget.JVM_17

    const val name = "TimerX"
    const val namespace = "com.timerx"
    const val versionCode = 1
    const val majorRelease = 1
    const val minorRelease = 0
    const val patch = 1
    const val majorVersionName = "$majorRelease.$minorRelease.$patch"
    const val versionName = "$majorRelease.$minorRelease.$patch"
    const val appDescription = "The best timer in the world"
    const val vendorName = "TimerX"
    const val licenseFile = "LICENSE.txt"
    val javaVersion = JavaVersion.VERSION_17
    const val compileSdk = 35
    const val targetSdk = compileSdk
    const val minSdk = 30
    const val appId = "todo-some-uuid"
    const val supportEmail = "emailthis@mail.com"

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
