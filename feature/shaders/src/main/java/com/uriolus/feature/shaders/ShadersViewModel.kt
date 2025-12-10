package com.uriolus.feature.shaders

import com.uriolus.core.common.mvi.MviViewModel

/**
 * ViewModel for shader editor following MVI pattern
 */
class ShadersViewModel : MviViewModel<ShadersState, ShadersIntent, ShadersEvent>(
    initialState = ShadersState()
) {
    
    override fun handleIntent(intent: ShadersIntent) {
        when (intent) {
            is ShadersIntent.UpdateShaderCode -> {
                updateState { copy(shaderCode = intent.code, compileError = null) }
            }
            
            is ShadersIntent.SelectPreset -> {
                val preset = SHADER_PRESETS.find { it.id == intent.presetId }
                if (preset != null) {
                    updateState {
                        copy(
                            selectedPresetId = preset.id,
                            shaderCode = preset.code,
                            compileError = null,
                            showPresets = false
                        )
                    }
                    sendEvent(ShadersEvent.ShowMessage("Loaded preset: ${preset.name}"))
                }
            }
            
            is ShadersIntent.UpdateCustomUniform -> {
                updateState {
                    copy(customUniforms = customUniforms + (intent.name to intent.value))
                }
            }
            
            is ShadersIntent.ToggleControls -> {
                updateState { copy(showControls = !showControls) }
            }
            
            is ShadersIntent.TogglePresets -> {
                updateState { copy(showPresets = !showPresets) }
            }
            
            is ShadersIntent.ToggleEditor -> {
                updateState { copy(isEditorExpanded = !isEditorExpanded) }
            }
            
            is ShadersIntent.ResetShader -> {
                val defaultPreset = SHADER_PRESETS.first()
                updateState {
                    copy(
                        shaderCode = defaultPreset.code,
                        selectedPresetId = defaultPreset.id,
                        compileError = null,
                        customUniforms = emptyMap()
                    )
                }
                sendEvent(ShadersEvent.ShowMessage("Shader reset"))
            }
        }
    }
    
    // Called from the UI to update time
    fun updateTime(time: Float) {
        updateState { copy(elapsedTime = time) }
    }
    
    // Called from the UI to update touch position
    fun updateTouchPosition(x: Float, y: Float) {
        updateState { copy(touchPosition = androidx.compose.ui.geometry.Offset(x, y)) }
    }
}
