package com.intervallum.util

fun String.capitalize(): String =
    this.replaceFirstChar { name ->
        if (name.isLowerCase()) name.uppercaseChar()
        else name
    }

fun String?.isValid(): Boolean {
    kotlin.contracts.contract {
        returns(true) implies (this@isValid != null)
    }
    return !isNullOrBlank() && !equals("null", true)
}

fun String?.takeIfValid(): String? = if (isValid()) this else null
