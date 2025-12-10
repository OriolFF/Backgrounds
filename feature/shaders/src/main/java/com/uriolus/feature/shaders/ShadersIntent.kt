package com.uriolus.feature.shaders

/**
 * User intents for shader editor
 */
sealed interface ShadersIntent {
    data class UpdateShaderCode(val code: String) : ShadersIntent
    data class SelectPreset(val presetId: String) : ShadersIntent
    data class UpdateCustomUniform(val name: String, val value: Float) : ShadersIntent
    data object ToggleControls : ShadersIntent
    data object TogglePresets : ShadersIntent
    data object ToggleEditor : ShadersIntent
    data object ResetShader : ShadersIntent
}
