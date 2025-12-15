package com.uriolus.core.ui.shader

import android.graphics.RuntimeShader
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A composable that renders an AGSL shader as a background.
 * 
 * @param shaderCode The AGSL shader code to render
 * @param modifier Modifier for the container
 * @param enableTouchInput Whether to enable touch input (iMouse uniform), defaults to false
 * @param onError Optional callback when shader compilation fails
 * @param content Optional content to overlay on top of the shader background
 */
@Composable
fun ShaderBackground(
    shaderCode: String,
    modifier: Modifier = Modifier,
    enableTouchInput: Boolean = false,
    onError: ((Exception) -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    var time by remember { mutableStateOf(0f) }
    var touchPosition by remember { mutableStateOf(Offset.Zero) }
    
    // Time animation
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            kotlinx.coroutines.delay(16) // ~60 FPS
            time = (System.currentTimeMillis() - startTime) / 1000f
        }
    }
    
    // Create shader - catch compilation errors
    val shader = remember(shaderCode) {
        try {
            RuntimeShader(shaderCode)
        } catch (e: Exception) {
            onError?.invoke(e)
            null
        }
    }
    
    Box(
        modifier = modifier
            .then(
                if (enableTouchInput) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures { offset ->
                            touchPosition = offset
                        }
                    }
                } else {
                    Modifier
                }
            )
            .drawBehind {
                if (shader != null) {
                    try {
                        // Set uniforms
                        shader.setFloatUniform("iTime", time)
                        shader.setFloatUniform("iResolution", this.size.width, this.size.height)
                        
                        // Only set iMouse if touch input is enabled
                        if (enableTouchInput) {
                            try {
                                shader.setFloatUniform("iMouse", touchPosition.x, touchPosition.y)
                            } catch (e: Exception) {
                                // Shader might not have iMouse uniform, ignore
                            }
                        }
                        
                        drawRect(
                            brush = ShaderBrush(shader),
                            size = this.size
                        )
                    } catch (e: Exception) {
                        // Fallback: draw dark gray
                        drawRect(Color.DarkGray)
                        onError?.invoke(e)
                    }
                } else {
                    // Shader failed to compile - use fallback color
                    drawRect(Color.DarkGray)
                }
            }
    ) {
        content()
    }
}
