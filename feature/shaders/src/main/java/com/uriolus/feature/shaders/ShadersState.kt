package com.uriolus.feature.shaders

import androidx.compose.ui.geometry.Offset

/**
 * State for the shader editor screen
 */
data class ShadersState(
    val shaderCode: String = DEFAULT_SHADER,
    val selectedPresetId: String = "gradient",
    val compileError: String? = null,
    val elapsedTime: Float = 0f,
    val touchPosition: Offset = Offset.Zero,
    val customUniforms: Map<String, Float> = emptyMap(),
    val showControls: Boolean = true,
    val showPresets: Boolean = false,
    val isEditorExpanded: Boolean = false
)

// Default simple gradient shader
private const val DEFAULT_SHADER = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    
    // Animated gradient
    float r = 0.5 + 0.5 * sin(iTime + uv.x * 3.0);
    float g = 0.5 + 0.5 * sin(iTime + uv.y * 3.0 + 2.0);
    float b = 0.5 + 0.5 * sin(iTime + (uv.x + uv.y) * 3.0 + 4.0);
    
    return half4(r, g, b, 1.0);
}
"""

/**
 * Shader preset definition
 */
data class ShaderPreset(
    val id: String,
    val name: String,
    val description: String,
    val code: String
)

/**
 * Available shader presets
 */
val SHADER_PRESETS = listOf(
    ShaderPreset(
        id = "gradient",
        name = "Gradient",
        description = "Animated color gradient",
        code = DEFAULT_SHADER
    ),
    ShaderPreset(
        id = "waves",
        name = "Waves",
        description = "Sine wave pattern",
        code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    
    // Wave effect
    float wave = sin(uv.x * 10.0 + iTime) * 0.5 + 0.5;
    wave += sin(uv.y * 10.0 + iTime * 0.7) * 0.5 + 0.5;
    wave *= 0.5;
    
    float3 color = float3(wave * 0.3, wave * 0.6, wave);
    
    return half4(color, 1.0);
}
"""
    ),
    ShaderPreset(
        id = "circles",
        name = "Circles",
        description = "Concentric circles",
        code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    uv = uv * 2.0 - 1.0; // Center coordinates
    uv.x *= iResolution.x / iResolution.y; // Correct aspect ratio
    
    float dist = length(uv);
    float pattern = sin(dist * 10.0 - iTime * 2.0) * 0.5 + 0.5;
    
    float3 color = float3(pattern * 0.8, pattern * 0.4, pattern);
    
    return half4(color, 1.0);
}
"""
    ),
    ShaderPreset(
        id = "plasma",
        name = "Plasma",
        description = "Plasma effect",
        code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    
    float t = iTime * 0.5;
    
    float v1 = sin(uv.x * 10.0 + t);
    float v2 = sin(uv.y * 10.0 + t);
    float v3 = sin((uv.x + uv.y) * 10.0 + t);
    float v4 = sin(sqrt(uv.x * uv.x + uv.y * uv.y) * 10.0 + t);
    
    float composite = (v1 + v2 + v3 + v4) * 0.25;
    
    float r = 0.5 + 0.5 * sin(composite * 3.14159);
    float g = 0.5 + 0.5 * sin(composite * 3.14159 + 2.0);
    float b = 0.5 + 0.5 * sin(composite * 3.14159 + 4.0);
    
    return half4(r, g, b, 1.0);
}
"""
    ),
    ShaderPreset(
        id = "tunnel",
        name = "Tunnel",
        description = "Tunnel effect",
        code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    uv = uv * 2.0 - 1.0;
    uv.x *= iResolution.x / iResolution.y;
    
    float dist = length(uv);
    float angle = atan(uv.y, uv.x);
    
    float r = 1.0 / dist;
    float pattern = sin(r * 5.0 - iTime * 2.0) * 0.5 + 0.5;
    float stripes = sin(angle * 8.0) * 0.5 + 0.5;
    
    float3 color = float3(pattern * stripes, pattern * 0.5, pattern);
    
    return half4(color, 1.0);
}
"""
    )
)
