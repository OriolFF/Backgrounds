package com.uriolus.feature.shaders

import android.graphics.RuntimeShader
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadersScreen(
    viewModel: ShadersViewModel = koinViewModel(),
    onNavigateToHelp: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Time animation
    var time by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            kotlinx.coroutines.delay(16) // ~60 FPS
            time = (System.currentTimeMillis() - startTime) / 1000f
            viewModel.updateTime(time)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ShadersEvent.ShowMessage -> {
                    // Could show a snackbar here
                }
                is ShadersEvent.CompileError -> {
                    errorMessage = event.error
                    showError = true
                }
                is ShadersEvent.ShaderCompiled -> {
                    showError = false
                }
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top shader preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (state.isEditorExpanded) 0.3f else 0.6f)
            ) {
                ShaderPreview(
                    shaderCode = state.shaderCode,
                    time = state.elapsedTime,
                    onTouchUpdate = { x, y -> viewModel.updateTouchPosition(x, y) },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Top bar overlay
                if (state.showControls) {
                    TopAppBar(
                        title = { Text("Shader Editor", style = MaterialTheme.typography.titleMedium) },
                        actions = {
                            IconButton(onClick = onNavigateToHelp) {
                                Icon(Icons.Default.Info, "Help", tint = Color.White)
                            }
                            IconButton(onClick = { viewModel.handleIntent(ShadersIntent.TogglePresets) }) {
                                Icon(Icons.Default.List, "Presets", tint = Color.White)
                            }
                            IconButton(onClick = { viewModel.handleIntent(ShadersIntent.ResetShader) }) {
                                Icon(Icons.Default.Refresh, "Reset", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            titleContentColor = Color.White
                        ),
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
                
                // Error display
                if (showError) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Bottom code editor
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (state.isEditorExpanded) 0.7f else 0.4f),
                color = Color(0xFF1E1E1E),
                tonalElevation = 8.dp
            ) {
                Column {
                    // Editor toolbar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2D2D2D))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "AGSL Shader Code",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(0.7f)
                        )
                        Row {
                            IconButton(
                                onClick = { viewModel.handleIntent(ShadersIntent.ToggleEditor) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    if (state.isEditorExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                    "Expand Editor",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    
                    // Code editor
                    CodeEditor(
                        code = state.shaderCode,
                        onCodeChange = { viewModel.handleIntent(ShadersIntent.UpdateShaderCode(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(8.dp)
                    )
                }
            }
        }
        
        // Preset selector overlay
        if (state.showPresets) {
            PresetSelector(
                presets = SHADER_PRESETS,
                selectedId = state.selectedPresetId,
                onPresetSelected = { viewModel.handleIntent(ShadersIntent.SelectPreset(it)) },
                onDismiss = { viewModel.handleIntent(ShadersIntent.TogglePresets) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 64.dp, end = 16.dp)
            )
        }
        
        // Toggle controls FAB
        FloatingActionButton(
            onClick = { viewModel.handleIntent(ShadersIntent.ToggleControls) },
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(
                if (state.showControls) Icons.Filled.Close else Icons.Filled.Menu,
                "Toggle Controls"
            )
        }
    }
}

@Composable
private fun ShaderPreview(
    shaderCode: String,
    time: Float,
    onTouchUpdate: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(Offset.Zero) }
    
    // Create shader - catch errors
    val shader = remember(shaderCode) {
        try {
            RuntimeShader(shaderCode)
        } catch (e: Exception) {
            null
        }
    }
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onTouchUpdate(offset.x, offset.y)
                }
            }
            .drawBehind {
                size = Offset(this.size.width, this.size.height)
                
                if (shader != null) {
                    try {
                        shader.setFloatUniform("iTime", time)
                        shader.setFloatUniform("iResolution", this.size.width, this.size.height)
                        
                        drawRect(
                            brush = ShaderBrush(shader),
                            size = this.size
                        )
                    } catch (e: Exception) {
                        // Fallback: draw error color
                        drawRect(Color.DarkGray)
                    }
                } else {
                    // Shader failed to compile
                    drawRect(Color.DarkGray)
                }
            }
    )
}

@Composable
private fun CodeEditor(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    BasicTextField(
        value = code,
        onValueChange = onCodeChange,
        modifier = modifier
            .verticalScroll(scrollState)
            .background(Color(0xFF1E1E1E))
            .padding(8.dp),
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = Color(0xFFD4D4D4),
            lineHeight = 18.sp
        ),
        decorationBox = { innerTextField ->
            Box {
                if (code.isEmpty()) {
                    Text(
                        "Write your AGSL shader code here...",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun PresetSelector(
    presets: List<ShaderPreset>,
    selectedId: String,
    onPresetSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(280.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D2D),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Shader Presets",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            
            presets.forEach { preset ->
                Surface(
                    onClick = {
                        onPresetSelected(preset.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = if (preset.id == selectedId) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    } else {
                        Color.Transparent
                    }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            preset.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            preset.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.6f)
                        )
                    }
                }
            }
        }
    }
}
