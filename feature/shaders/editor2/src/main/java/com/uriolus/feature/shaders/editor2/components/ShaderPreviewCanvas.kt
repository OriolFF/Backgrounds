package com.uriolus.feature.shaders.editor2.components

import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.android.awaitFrame

/**
 * Canvas component for rendering AGSL shaders
 */
@Composable
fun ShaderPreviewCanvas(
    shaderCode: String,
    elapsedTime: Float,
    touchPosition: Offset,
    resolution: Size,
    onSizeChanged: (Size) -> Unit,
    onTouchPositionChanged: (Offset) -> Unit,
    onCompileError: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentShader by remember { mutableStateOf<RuntimeShader?>(null) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var lastError by remember { mutableStateOf<String?>(null) }
    
    // Compile shader when code changes
    LaunchedEffect(shaderCode) {
        try {
            val shader = RuntimeShader(shaderCode)
            currentShader = shader
            lastError = null
            onCompileError(null)
        } catch (e: Exception) {
            currentShader = null
            val errorMsg = "Shader compilation error: ${e.message}"
            lastError = errorMsg
            onCompileError(errorMsg)
        }
    }
    
    // Update uniforms
    LaunchedEffect(currentShader, elapsedTime, touchPosition, canvasSize) {
        currentShader?.let { shader ->
            try {
                // Set iResolution uniform
                shader.setFloatUniform(
                    "iResolution",
                    canvasSize.width,
                    canvasSize.height
                )
                
                // Set iTime uniform
                shader.setFloatUniform("iTime", elapsedTime)
                
                // Set iMouse uniform if shader uses it
                try {
                    shader.setFloatUniform(
                        "iMouse",
                        touchPosition.x,
                        touchPosition.y
                    )
                } catch (e: Exception) {
                    // Shader doesn't have iMouse uniform, ignore
                }
            } catch (e: Exception) {
                // Ignore uniform update errors
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val newSize = size.toSize()
                canvasSize = newSize
                onSizeChanged(newSize)
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        onTouchPositionChanged(offset)
                    }
                )
            }
    ) {
        currentShader?.let { shader ->
            val brush = ShaderBrush(shader)
            drawRect(
                brush = brush,
                size = size
            )
        } ?: run {
            // Draw black background if shader not compiled
            drawRect(
                color = androidx.compose.ui.graphics.Color.Black,
                size = size
            )
        }
    }
}

/**
 * Animated shader preview that updates time automatically
 */
@Composable
fun AnimatedShaderPreview(
    shaderCode: String,
    touchPosition: Offset,
    resolution: Size,
    onTimeUpdate: (Float) -> Unit,
    onSizeChanged: (Size) -> Unit,
    onTouchPositionChanged: (Offset) -> Unit,
    onCompileError: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var elapsedTime by remember { mutableStateOf(0f) }
    
    // Animate time
    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            awaitFrame()
            val currentTime = (System.nanoTime() - startTime) / 1_000_000_000f
            elapsedTime = currentTime
            onTimeUpdate(currentTime)
        }
    }
    
    ShaderPreviewCanvas(
        shaderCode = shaderCode,
        elapsedTime = elapsedTime,
        touchPosition = touchPosition,
        resolution = resolution,
        onSizeChanged = onSizeChanged,
        onTouchPositionChanged = onTouchPositionChanged,
        onCompileError = onCompileError,
        modifier = modifier
    )
}
