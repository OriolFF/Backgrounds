package com.uriolus.feature.shaders

import androidx.compose.ui.geometry.Offset
import androidx.annotation.RawRes

/**
 * State for the shader editor screen
 */
data class ShadersState(
    val shaderCode: String = "",
    val selectedPresetId: String = "gradient",
    val compileError: String? = null,
    val elapsedTime: Float = 0f,
    val touchPosition: Offset = Offset.Zero,
    val customUniforms: Map<String, Float> = emptyMap(),
    val showControls: Boolean = true,
    val showPresets: Boolean = false,
    val isEditorExpanded: Boolean = false
)

/**
 * Shader preset definition
 * @param id Unique identifier for the preset
 * @param name Display name
 * @param description Brief description of the effect
 * @param resourceId Resource ID of the shader file in res/raw/
 */
data class ShaderPreset(
    val id: String,
    val name: String,
    val description: String,
    @RawRes val resourceId: Int
)
