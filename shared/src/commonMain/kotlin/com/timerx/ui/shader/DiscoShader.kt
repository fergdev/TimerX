package com.timerx.ui.shader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

private val shader = """
    uniform float2 resolution;
    uniform shader content; 
    uniform float time;
    vec4 main(vec2 fragcoord) {
        vec4 o = vec4(0);
        vec2 p = vec2(0), c=p, u=fragcoord.xy*2.-resolution.xy;
        float a; for (float i=0; i<4e2; i++) {
            a = i/2e2-1.;
            p = cos(i*2.4+time+vec2(0,11))*sqrt(1.-a*a);
            c = u/resolution.y+vec2(p.x,a)/(p.y+2.);
            o += (cos(i+vec4(0,2,4,0))+1.)/dot(c,c)*(1.-p.y)/3e4; 
        }
        return o; 
    }
""".trimIndent()

fun Modifier.discoShader(running: Boolean = true) = composed {
    var time by remember { mutableStateOf(0f) }
    FPSLaunchedEffect { time = it }
    this then runtimeShader(shader) {
        uniform("time", time)
    }
}

