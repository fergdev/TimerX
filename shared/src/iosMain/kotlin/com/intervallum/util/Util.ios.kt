package com.intervallum.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError

class NSErrorException(val nsError: NSError) : Exception(nsError.localizedDescription)

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
inline fun <T> throwNSErrors(block: (CPointer<ObjCObjectVar<NSError?>>) -> T): T = memScoped {
    val err = alloc<ObjCObjectVar<NSError?>>()
    val result = block(err.ptr)
    if (err.value != null) throw NSErrorException(err.value!!)
    result
}
