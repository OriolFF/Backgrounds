package com.uriolus.feature.shaders

import android.content.Context
import com.uriolus.core.common.mvi.MviViewModel

/**
 * ViewModel for shader editor following MVI pattern
 */
class ShadersViewModel(
    private val context: Context
) : MviViewModel<ShadersState, ShadersIntent, ShadersEvent>(
    initialState = ShadersState()
) {
    
    private val shaderPresets: List<ShaderPreset> by lazy {
        ShaderResourceLoader.loadPresets(context)
    }
    
    init {
        // Load default shader on init
        val defaultPreset = shaderPresets.firstOrNull()
        if (defaultPreset != null) {
            val code = ShaderResourceLoader.loadShaderCode(context, defaultPreset.resourceId)
            updateState { copy(shaderCode = code) }
        }
    }
    
    override fun handleIntent(intent: ShadersIntent) {
        when (intent) {
            is ShadersIntent.UpdateShaderCode -> {
                updateState { copy(shaderCode = intent.code, compileError = null) }
            }
            
            is ShadersIntent.SelectPreset -> {
                val preset = shaderPresets.find { it.id == intent.presetId }
                if (preset != null) {
                    val code = ShaderResourceLoader.loadShaderCode(context, preset.resourceId)
                    updateState {
                        copy(
                            selectedPresetId = preset.id,
                            shaderCode = code,
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
                val defaultPreset = shaderPresets.first()
                val code = ShaderResourceLoader.loadShaderCode(context, defaultPreset.resourceId)
                updateState {
                    copy(
                        shaderCode = code,
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
    
    // Get presets for UI
    fun getPresets(): List<ShaderPreset> = shaderPresets
}
