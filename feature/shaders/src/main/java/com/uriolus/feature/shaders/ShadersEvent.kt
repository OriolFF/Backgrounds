package com.uriolus.feature.shaders

/**
 * One-time events from the shader editor
 */
sealed interface ShadersEvent {
    data class ShowMessage(val message: String) : ShadersEvent
    data class CompileError(val error: String) : ShadersEvent
    data object ShaderCompiled : ShadersEvent
}
