package com.uriolus.feature.shaders.editor2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uriolus.feature.shaders.editor2.components.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Editor2 main screen composable
 */
@Composable
fun Editor2Screen(
    onNavigateBack: () -> Unit,
    onNavigateToHelp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: Editor2ViewModel = viewModel { Editor2ViewModel(context) }
    val state by viewModel.state.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is Editor2Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is Editor2Event.ScrollToCode -> {
                    // Scroll handled by code editor internally
                }
                is Editor2Event.ShaderSaved -> {
                    snackbarHostState.showSnackbar("Shader saved: ${event.file.displayName}")
                }
                is Editor2Event.CompileError -> {
                    snackbarHostState.showSnackbar("Compile error: ${event.error}")
                }
                is Editor2Event.ShaderCompiled -> {
                    // Optional: show success indicator
                }
            }
        }
    }
    
    // Detect keyboard visibility using WindowInsets
    val imeInsets = WindowInsets.ime
    val imeBottom = with(LocalDensity.current) { imeInsets.getBottom(this).toDp() }
    val isKeyboardVisible = imeBottom > 0.dp
    
    // Update keyboard visibility state
    LaunchedEffect(isKeyboardVisible) {
        viewModel.handleIntent(Editor2Intent.OnKeyboardVisibilityChanged(isKeyboardVisible))
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Shader preview as background (full screen)
        AnimatedShaderPreview(
            shaderCode = state.shaderCode,
            touchPosition = state.touchPosition,
            resolution = state.resolution,
            onTimeUpdate = { time -> viewModel.updateTime(time) },
            onSizeChanged = { size -> viewModel.updateResolution(size.width, size.height) },
            onTouchPositionChanged = { offset ->
                viewModel.updateTouchPosition(offset.x, offset.y)
            },
            onCompileError = { error ->
                viewModel.updateCompileStatus(
                    isCompiled = error == null,
                    error = error
                )
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // UI overlay
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TransparentTopBar(
                    title = state.currentShaderName.ifEmpty { "Shader Editor 2" },
                    alpha = state.topBarAlpha,
                    onNavigateBack = onNavigateBack,
                    onSaveClick = { viewModel.handleIntent(Editor2Intent.ShowSaveDialog) },
                    onLoadClick = { viewModel.handleIntent(Editor2Intent.ShowLoadDialog) },
                    onHelpClick = onNavigateToHelp
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.imePadding()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Spacer(modifier = Modifier.weight(1f))
                
                // Collapsible code editor section
                CollapsibleCodeSection(
                    isExpanded = state.isCodeExpanded,
                    onToggleExpanded = {
                        viewModel.handleIntent(Editor2Intent.ToggleCodeVisibility)
                    },
                    alpha = state.codeEditorAlpha
                ) {
                    CodeEditorField(
                        code = state.shaderCode,
                        onCodeChange = { code, cursorPos ->
                            viewModel.handleIntent(Editor2Intent.UpdateCode(code, cursorPos))
                        },
                        alpha = state.codeEditorAlpha
                    )
                }
            }
        }
        
        // Save dialog
        if (state.showSaveDialog) {
            SaveShaderDialog(
                onDismiss = { viewModel.handleIntent(Editor2Intent.HideSaveDialog) },
                onSave = { name ->
                    viewModel.handleIntent(Editor2Intent.SaveShader(name))
                }
            )
        }
        
        // Load dialog
        if (state.showLoadDialog) {
            LoadShaderDialog(
                presets = state.availablePresets,
                savedShaders = state.savedShaders,
                onDismiss = { viewModel.handleIntent(Editor2Intent.HideLoadDialog) },
                onLoadPreset = { presetId ->
                    viewModel.handleIntent(Editor2Intent.LoadPreset(presetId))
                },
                onLoadCustom = { shaderFile ->
                    viewModel.handleIntent(Editor2Intent.LoadCustomShader(shaderFile))
                },
                onDelete = { shaderFile ->
                    viewModel.handleIntent(Editor2Intent.DeleteShader(shaderFile))
                }
            )
        }
    }
}

/**
 * Dialog for saving shader with custom name
 */
@Composable
private fun SaveShaderDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var shaderName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Shader") },
        text = {
            Column {
                Text("Enter a name for your shader:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = shaderName,
                    onValueChange = { shaderName = it },
                    label = { Text("Shader name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (shaderName.isNotBlank()) {
                        onSave(shaderName)
                    }
                },
                enabled = shaderName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog for loading presets or saved shaders
 */
@Composable
private fun LoadShaderDialog(
    presets: List<com.uriolus.feature.shaders.ShaderPreset>,
    savedShaders: List<com.uriolus.feature.shaders.editor2.storage.ShaderFile>,
    onDismiss: () -> Unit,
    onLoadPreset: (String) -> Unit,
    onLoadCustom: (com.uriolus.feature.shaders.editor2.storage.ShaderFile) -> Unit,
    onDelete: (com.uriolus.feature.shaders.editor2.storage.ShaderFile) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Load Shader") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Presets section
                item {
                    Text(
                        text = "Presets",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(presets) { preset ->
                    ListItem(
                        headlineContent = { Text(preset.name) },
                        supportingContent = { Text(preset.description) },
                        modifier = Modifier.clickable {
                            onLoadPreset(preset.id)
                        }
                    )
                }
                
                // Saved shaders section
                if (savedShaders.isNotEmpty()) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "My Shaders",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(savedShaders) { shaderFile ->
                        ListItem(
                            headlineContent = { Text(shaderFile.displayName) },
                            supportingContent = {
                                Text(
                                    "Modified: ${
                                        java.text.SimpleDateFormat("MMM dd, yyyy HH:mm")
                                            .format(java.util.Date(shaderFile.modifiedAt))
                                    }"
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { onDelete(shaderFile) }) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                onLoadCustom(shaderFile)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
