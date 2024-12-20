package com.timerx.ui.shader

import androidx.compose.ui.Modifier

private val shader = """
    uniform float2 resolution;
    uniform shader content; 
    uniform float intensity;
    uniform float decayFactor;

    half4 main(vec2 fragCoord) {
        vec2 uv = fragCoord.xy / resolution.xy;
        half4 color = content.eval(fragCoord);
        uv *=  1.0 - uv.yx;
        float vig = clamp(uv.x*uv.y * intensity, 0., 1.);
        vig = pow(vig, decayFactor);
        return half4(vig * color.rgb, color.a);
    }
""".trimIndent()

fun Modifier.vignetteShader(
    intensity: Float = 100f,
    decayFactor: Float = 0.25f,
) = this then runtimeShader(shader) {
    uniform("intensity", intensity)
    uniform("decayFactor", decayFactor)
}
