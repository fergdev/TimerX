package com.timerx.util

import com.timerx.BuildFlags
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val Json = Json {
    prettyPrint = BuildFlags.debuggable
    decodeEnumsCaseInsensitive = true
    explicitNulls = false
    coerceInputValues = true
    allowTrailingComma = true
    useAlternativeNames = true
}
