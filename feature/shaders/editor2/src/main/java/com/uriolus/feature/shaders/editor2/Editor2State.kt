package com.uriolus.feature.shaders.editor2

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.uriolus.feature.shaders.ShaderPreset
import com.uriolus.feature.shaders.editor2.storage.ShaderFile

/**
 * State for the Editor2 screen following MVI pattern
 */
data class Editor2State(
    // Code editing
    val shaderCode: String = "",
    val cursorPosition: Int = 0,
    
    // UI state
    val isCodeExpanded: Boolean = true,
    val isKeyboardVisible: Boolean = false,
    val topBarAlpha: Float = 0.3f,
    val codeEditorAlpha: Float = 0.85f,
    
    // Shader state
    val currentShaderName: String = "",
    val isPreset: Boolean = true,
    val compileError: String? = null,
    val isCompiled: Boolean = false,
    
    // Runtime uniforms
    val elapsedTime: Float = 0f,
    val touchPosition: Offset = Offset.Zero,
    val resolution: Size = Size.Zero,
    
    // Available shaders
    val availablePresets: List<ShaderPreset> = emptyList(),
    val savedShaders: List<ShaderFile> = emptyList(),
    
    // Save/Load state
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showLoadDialog: Boolean = false
)
