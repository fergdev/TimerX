@file:Suppress("MissingPackageDeclaration")
fun stabilityLevel(version: String): Int {
    Config.stabilityLevels.forEachIndexed { index, postfix ->
        val regex = """.*[.\-]$postfix[.\-\d]*""".toRegex(RegexOption.IGNORE_CASE)
        if (version.matches(regex)) return index
    }
    return Config.stabilityLevels.size
}
