package com.uriolus.feature.shaders.editor2

import com.uriolus.feature.shaders.editor2.storage.ShaderFile

/**
 * User intents for Editor2 following MVI pattern
 */
sealed interface Editor2Intent {
    // Code editing
    data class UpdateCode(val code: String, val cursorPosition: Int = 0) : Editor2Intent
    
    // UI controls
    data object ToggleCodeVisibility : Editor2Intent
    data class OnKeyboardVisibilityChanged(val isVisible: Boolean) : Editor2Intent
    
    // Shader management
    data class SaveShader(val name: String) : Editor2Intent
    data class LoadPreset(val presetId: String) : Editor2Intent
    data class LoadCustomShader(val shaderFile: ShaderFile) : Editor2Intent
    data class DeleteShader(val shaderFile: ShaderFile) : Editor2Intent
    
    // Dialog controls
    data object ShowSaveDialog : Editor2Intent
    data object HideSaveDialog : Editor2Intent
    data object ShowLoadDialog : Editor2Intent
    data object HideLoadDialog : Editor2Intent
    
    // Runtime updates
    data class UpdateCursorPosition(val position: Int) : Editor2Intent
}
