package com.intervallum.util

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class KoverIgnore(@Suppress("unused") val reason: String)
