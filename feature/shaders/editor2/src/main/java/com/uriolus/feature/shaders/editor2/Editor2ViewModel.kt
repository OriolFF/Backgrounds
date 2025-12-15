package com.uriolus.feature.shaders.editor2

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.uriolus.core.common.mvi.MviViewModel
import com.uriolus.feature.shaders.ShaderResourceLoader
import com.uriolus.feature.shaders.editor2.storage.ShaderStorage
import kotlinx.coroutines.launch

/**
 * ViewModel for Editor2 following MVI pattern
 */
class Editor2ViewModel(
    private val context: Context
) : MviViewModel<Editor2State, Editor2Intent, Editor2Event>(
    initialState = Editor2State()
) {
    
    private val shaderStorage = ShaderStorage(context)
    
    init {
        loadInitialData()
    }
    
    /**
     * Load presets and saved shaders on init
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            // Load presets from resources
            val presets = ShaderResourceLoader.loadPresets(context)
            updateState { copy(availablePresets = presets) }
            
            // Load saved shaders from storage
            val savedShaders = shaderStorage.listSavedShaders()
            updateState { copy(savedShaders = savedShaders) }
            
            // Load first preset as default
            val defaultPreset = presets.firstOrNull()
            if (defaultPreset != null) {
                val code = ShaderResourceLoader.loadShaderCode(context, defaultPreset.resourceId)
                updateState {
                    copy(
                        shaderCode = code,
                        currentShaderName = defaultPreset.name,
                        isPreset = true,
                        isCompiled = true
                    )
                }
            }
        }
    }
    
    override fun handleIntent(intent: Editor2Intent) {
        when (intent) {
            is Editor2Intent.UpdateCode -> {
                updateState {
                    copy(
                        shaderCode = intent.code,
                        cursorPosition = intent.cursorPosition,
                        compileError = null
                    )
                }
                // Note: Shader compilation happens in the UI layer (ShaderPreviewCanvas)
                // to avoid blocking the ViewModel
            }
            
            is Editor2Intent.ToggleCodeVisibility -> {
                updateState { copy(isCodeExpanded = !isCodeExpanded) }
            }
            
            is Editor2Intent.OnKeyboardVisibilityChanged -> {
                updateState { copy(isKeyboardVisible = intent.isVisible) }
                if (intent.isVisible) {
                    // Scroll to cursor position when keyboard appears
                    sendEvent(Editor2Event.ScrollToCode(state.value.cursorPosition))
                }
            }
            
            is Editor2Intent.SaveShader -> {
                saveShader(intent.name)
            }
            
            is Editor2Intent.LoadPreset -> {
                loadPreset(intent.presetId)
            }
            
            is Editor2Intent.LoadCustomShader -> {
                loadCustomShader(intent.shaderFile)
            }
            
            is Editor2Intent.DeleteShader -> {
                deleteShader(intent.shaderFile)
            }
            
            is Editor2Intent.ShowSaveDialog -> {
                updateState { copy(showSaveDialog = true) }
            }
            
            is Editor2Intent.HideSaveDialog -> {
                updateState { copy(showSaveDialog = false) }
            }
            
            is Editor2Intent.ShowLoadDialog -> {
                updateState { copy(showLoadDialog = true) }
            }
            
            is Editor2Intent.HideLoadDialog -> {
                updateState { copy(showLoadDialog = false) }
            }
            
            is Editor2Intent.UpdateCursorPosition -> {
                updateState { copy(cursorPosition = intent.position) }
            }
        }
    }
    
    /**
     * Save current shader code to file
     */
    private fun saveShader(name: String) {
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            
            val result = shaderStorage.saveShader(name, state.value.shaderCode)
            
            result.onSuccess { shaderFile ->
                // Refresh saved shaders list
                val savedShaders = shaderStorage.listSavedShaders()
                updateState {
                    copy(
                        savedShaders = savedShaders,
                        currentShaderName = shaderFile.displayName,
                        isPreset = false,
                        isSaving = false,
                        showSaveDialog = false
                    )
                }
                sendEvent(Editor2Event.ShaderSaved(shaderFile))
                sendEvent(Editor2Event.ShowMessage("Shader saved: ${shaderFile.displayName}"))
            }
            
            result.onFailure { error ->
                updateState { copy(isSaving = false) }
                sendEvent(Editor2Event.ShowMessage("Failed to save shader: ${error.message}"))
            }
        }
    }
    
    /**
     * Load preset shader from resources
     */
    private fun loadPreset(presetId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            
            val preset = state.value.availablePresets.find { it.id == presetId }
            if (preset != null) {
                val code = ShaderResourceLoader.loadShaderCode(context, preset.resourceId)
                updateState {
                    copy(
                        shaderCode = code,
                        currentShaderName = preset.name,
                        isPreset = true,
                        compileError = null,
                        isLoading = false,
                        showLoadDialog = false,
                        isCompiled = true
                    )
                }
                sendEvent(Editor2Event.ShowMessage("Loaded preset: ${preset.name}"))
            } else {
                updateState { copy(isLoading = false) }
                sendEvent(Editor2Event.ShowMessage("Preset not found"))
            }
        }
    }
    
    /**
     * Load custom shader from file
     */
    private fun loadCustomShader(shaderFile: com.uriolus.feature.shaders.editor2.storage.ShaderFile) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            
            val result = shaderStorage.loadShader(shaderFile)
            
            result.onSuccess { code ->
                updateState {
                    copy(
                        shaderCode = code,
                        currentShaderName = shaderFile.displayName,
                        isPreset = false,
                        compileError = null,
                        isLoading = false,
                        showLoadDialog = false,
                        isCompiled = true
                    )
                }
                sendEvent(Editor2Event.ShowMessage("Loaded shader: ${shaderFile.displayName}"))
            }
            
            result.onFailure { error ->
                updateState { copy(isLoading = false) }
                sendEvent(Editor2Event.ShowMessage("Failed to load shader: ${error.message}"))
            }
        }
    }
    
    /**
     * Delete custom shader file
     */
    private fun deleteShader(shaderFile: com.uriolus.feature.shaders.editor2.storage.ShaderFile) {
        viewModelScope.launch {
            val result = shaderStorage.deleteShader(shaderFile)
            
            result.onSuccess {
                // Refresh saved shaders list
                val savedShaders = shaderStorage.listSavedShaders()
                updateState { copy(savedShaders = savedShaders) }
                sendEvent(Editor2Event.ShowMessage("Shader deleted: ${shaderFile.displayName}"))
            }
            
            result.onFailure { error ->
                sendEvent(Editor2Event.ShowMessage("Failed to delete shader: ${error.message}"))
            }
        }
    }
    
    /**
     * Update elapsed time for animation
     */
    fun updateTime(time: Float) {
        updateState { copy(elapsedTime = time) }
    }
    
    /**
     * Update touch position for iMouse uniform
     */
    fun updateTouchPosition(x: Float, y: Float) {
        updateState { copy(touchPosition = androidx.compose.ui.geometry.Offset(x, y)) }
    }
    
    /**
     * Update resolution for iResolution uniform
     */
    fun updateResolution(width: Float, height: Float) {
        updateState { copy(resolution = androidx.compose.ui.geometry.Size(width, height)) }
    }
    
    /**
     * Update compile status
     */
    fun updateCompileStatus(isCompiled: Boolean, error: String? = null) {
        updateState {
            copy(
                isCompiled = isCompiled,
                compileError = error
            )
        }
        if (error != null) {
            sendEvent(Editor2Event.CompileError(error))
        } else if (isCompiled) {
            sendEvent(Editor2Event.ShaderCompiled)
        }
    }
}
