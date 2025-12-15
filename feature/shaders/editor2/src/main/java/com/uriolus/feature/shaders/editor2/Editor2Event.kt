package com.uriolus.feature.shaders.editor2

import com.uriolus.feature.shaders.editor2.storage.ShaderFile

/**
 * One-time events from Editor2 following MVI pattern
 */
sealed interface Editor2Event {
    data class ShowMessage(val message: String) : Editor2Event
    data class ScrollToCode(val position: Int) : Editor2Event
    data class ShaderSaved(val file: ShaderFile) : Editor2Event
    data class CompileError(val error: String) : Editor2Event
    data object ShaderCompiled : Editor2Event
}
