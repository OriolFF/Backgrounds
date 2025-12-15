package com.uriolus.feature.shaders

import android.content.Context
import androidx.annotation.RawRes
import com.uriolus.feature.shaders.R

/**
 * Utility to load shader code from raw resources
 */
object ShaderResourceLoader {
    
    fun loadShaderCode(context: Context, @RawRes resourceId: Int): String {
        return context.resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }
    }
    
    /**
     * Load all shader presets from resources
     */
    fun loadPresets(context: Context): List<ShaderPreset> {
        return listOf(
            ShaderPreset(
                id = "gradient",
                name = "Gradient",
                description = "Animated color gradient",
                resourceId = R.raw.shader_gradient
            ),
            ShaderPreset(
                id = "waves",
                name = "Waves",
                description = "Sine wave pattern",
                resourceId = R.raw.shader_waves
            ),
            ShaderPreset(
                id = "circles",
                name = "Circles",
                description = "Concentric circles",
                resourceId = R.raw.shader_circles
            ),
            ShaderPreset(
                id = "plasma",
                name = "Plasma",
                description = "Plasma effect",
                resourceId = R.raw.shader_plasma
            ),
            ShaderPreset(
                id = "tunnel",
                name = "Tunnel",
                description = "Tunnel effect",
                resourceId = R.raw.shader_tunnel
            ),
            ShaderPreset(
                id = "ripple",
                name = "Ripple",
                description = "Interactive ripple effect (touch to interact)",
                resourceId = R.raw.shader_ripple
            )
        )
    }
}
