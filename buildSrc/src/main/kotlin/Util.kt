@file:Suppress("MissingPackageDeclaration")

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties

fun stabilityLevel(version: String): Int {
    Config.stabilityLevels.forEachIndexed { index, postfix ->
        val regex = """.*[.\-]$postfix[.\-\d]*""".toRegex(RegexOption.IGNORE_CASE)
        if (version.matches(regex)) return index
    }
    return Config.stabilityLevels.size
}

fun Project.localProperties() = lazy {
    Properties().apply {
        val file = File(rootProject.rootDir.absolutePath, "local.properties")
        if (!file.exists()) {
            println("w: Local.properties file does not exist. You may be missing some publishing keys")
            return@apply
        }
        load(FileInputStream(file))
    }
}
