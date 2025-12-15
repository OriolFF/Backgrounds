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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
    
    // Detect keyboard visibility
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    val isKeyboardVisible by remember {
        derivedStateOf {
            imeInsets.getBottom(density) > 0
        }
    }
    
    // Log keyboard state changes for debugging
    LaunchedEffect(isKeyboardVisible) {
        android.util.Log.d("ShadersScreen", "Keyboard visible: $isKeyboardVisible, IME bottom: ${imeInsets.getBottom(density)}")
    }
    
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
                    .then(
                        if (isKeyboardVisible) {
                            // Fixed compact height when keyboard visible
                            Modifier.height(180.dp)
                        } else if (state.isEditorExpanded) {
                            Modifier.weight(0.3f)
                        } else {
                            Modifier.weight(0.6f)
                        }
                    )
            ) {
                ShaderPreview(
                    shaderCode = state.shaderCode,
                    time = state.elapsedTime,
                    touchPosition = state.touchPosition,
                    onTouchUpdate = { x, y -> viewModel.updateTouchPosition(x, y) },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Top bar overlay - hide when keyboard is visible to save space
                if (state.showControls && !isKeyboardVisible) {
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
                    .then(
                        if (isKeyboardVisible) {
                            // Fill remaining space to eliminate black bar
                            Modifier.weight(1f)
                        } else if (state.isEditorExpanded) {
                            Modifier.weight(0.7f)
                        } else {
                            Modifier.weight(0.4f)
                        }
                    ),
                color = if (isKeyboardVisible) Color.Transparent else Color(0xFF1E1E1E),
                tonalElevation = 8.dp
            ) {
                Column(modifier = if (isKeyboardVisible) Modifier.fillMaxSize() else Modifier) {
                    // Editor toolbar - hide when keyboard is visible to maximize code space
                    if (!isKeyboardVisible) {
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
                    }
                    
                    // Code editor
                    CodeEditor(
                        code = state.shaderCode,
                        onCodeChange = { viewModel.handleIntent(ShadersIntent.UpdateShaderCode(it)) },
                        isKeyboardVisible = isKeyboardVisible,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
            }
        }
        
        // Preset selector overlay
        if (state.showPresets) {
            PresetSelector(
                presets = viewModel.getPresets(),
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
    touchPosition: Offset,
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
                        shader.setFloatUniform("iMouse", touchPosition.x, touchPosition.y)
                        
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
    isKeyboardVisible: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    
    // Don't recreate TextFieldValue on every code change - this causes cursor reset!
    var textFieldValue by remember { 
        mutableStateOf(TextFieldValue(code))
    }
    
    // Only update from external code changes (like preset selection)
    LaunchedEffect(code) {
        if (code != textFieldValue.text) {
            textFieldValue = TextFieldValue(
                text = code,
                selection = textFieldValue.selection
            )
        }
    }
    
    var showAutocomplete by remember { mutableStateOf(false) }
    var autocompleteItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSuggestionIndex by remember { mutableStateOf(0) }
    
    // AGSL autocomplete keywords
    val agslKeywords = remember {
        listOf(
            // Data types
            "float", "vec2", "vec3", "vec4", "mat2", "mat3", "mat4", 
            "half", "half2", "half3", "half4", "bool", "int",
            // Qualifiers
            "uniform", "in", "out", "const", "lowp", "mediump", "highp",
            // Built-in functions - Math
            "sin", "cos", "tan", "asin", "acos", "atan",
            "abs", "sqrt", "pow", "exp", "exp2", "log", "log2",
            "floor", "ceil", "fract", "mod", "min", "max", "clamp",
            "mix", "step", "smoothstep", "sign",
            // Vector functions
            "dot", "cross", "length", "distance", "normalize",
            "reflect", "refract", "faceforward",
            // Common functions
            "texture", "sample",
            // Uniforms
            "iTime", "iResolution", "iMouse",
            // Control flow
            "if", "else", "for", "while", "return",
            // Shader structure
            "shader", "vec", "mat"
        ).sorted()
    }
    
    // Get current word being typed
    fun getCurrentWord(): String {
        val cursorPos = textFieldValue.selection.start
        val textBeforeCursor = textFieldValue.text.substring(0, cursorPos)
        val lastWordStart = textBeforeCursor.indexOfLast { 
            it.isWhitespace() || it in "(){}[];,+-*/<>=!&|"
        } + 1
        return textBeforeCursor.substring(lastWordStart)
    }
    
    // Update autocomplete suggestions
    fun updateAutocomplete() {
        val currentWord = getCurrentWord()
        if (currentWord.length >= 2) {
            val filtered = agslKeywords.filter { 
                it.startsWith(currentWord, ignoreCase = true) && it != currentWord
            }
            if (filtered.isNotEmpty()) {
                autocompleteItems = filtered.take(5)
                showAutocomplete = true
                selectedSuggestionIndex = 0
            } else {
                showAutocomplete = false
            }
        } else {
            showAutocomplete = false
        }
    }
    
    // Apply autocomplete
    fun applyAutocomplete(suggestion: String) {
        val currentWord = getCurrentWord()
        val cursorPos = textFieldValue.selection.start
        val newText = textFieldValue.text.substring(0, cursorPos - currentWord.length) + 
                      suggestion + 
                      textFieldValue.text.substring(cursorPos)
        val newCursorPos = cursorPos - currentWord.length + suggestion.length
        
        textFieldValue = TextFieldValue(
            text = newText,
            selection = androidx.compose.ui.text.TextRange(newCursorPos)
        )
        onCodeChange(newText)
        showAutocomplete = false
    }
    
    Box(modifier = modifier) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onCodeChange(newValue.text)
                updateAutocomplete()
                // Bring cursor into view
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .bringIntoViewRequester(bringIntoViewRequester)
                .verticalScroll(scrollState)
                .background(Color(0xFF1E1E1E))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = Color(0xFFD4D4D4),
                lineHeight = 18.sp
            ),
            cursorBrush = SolidColor(Color.White),  // Make cursor visible
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Ascii
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (textFieldValue.text.isEmpty()) {
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
        
        // Autocomplete dropdown
        if (showAutocomplete && autocompleteItems.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 8.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF2D2D2D),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    autocompleteItems.forEachIndexed { index, item ->
                        Surface(
                            onClick = { applyAutocomplete(item) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp),
                            shape = RoundedCornerShape(2.dp),
                            color = if (index == selectedSuggestionIndex) {
                                Color(0xFF0D47A1).copy(alpha = 0.6f)
                            } else {
                                Color.Transparent
                            }
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color(0xFFD4D4D4)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
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
