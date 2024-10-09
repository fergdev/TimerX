package com.timerx.ui.shader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

private val shader = """
    uniform float2 resolution;
    uniform shader content; 
    uniform float time;
    
    float f(vec3 p) {
        p.z -= time * 10.;
        float a = p.z *.1;
        p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
        return .1 - length(cos(p.xy) + sin(p.yz));
    }
    
    half4 main(vec2 fragcoord) {
        vec3 d = .5 - fragcoord.xy1 / resolution.y;
        vec3 p = vec3(0);
        for (int i = 0; i < 32; i++) {
            p += f(p) * d; 
        }
        return ((sin(p) + vec3(2,5,9)) / length(p)).xyz1;
    }
""".trimIndent()

fun Modifier.wormHoleShader() = composed {
    var time by remember { mutableFloatStateOf(0f) }
    FPSLaunchedEffect { time = it }
    this then runtimeShader(shader) {
        uniform("time", time)
    }
}
