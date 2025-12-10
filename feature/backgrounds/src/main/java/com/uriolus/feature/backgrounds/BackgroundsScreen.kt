package com.uriolus.feature.backgrounds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundsScreen(
    viewModel: BackgroundsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var generatedCode by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BackgroundsEvent.ShowMessage -> println("Event: ${event.message}")
                is BackgroundsEvent.ThemeFilesGenerated -> {
                    generatedCode = event.filePath
                    showDialog = true
                }
                is BackgroundsEvent.GenerationError -> println("Error: ${event.error}")
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Full background
        CreativePatternBackground(
            state = state,
            modifier = Modifier.fillMaxSize()
        )
        
        // Top bar
        AnimatedVisibility(
            visible = state.showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopAppBar(
                title = { Text(state.patternType.displayName, style = MaterialTheme.typography.titleMedium) },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(BackgroundsIntent.RandomizePattern) }) {
                        Icon(Icons.Default.Refresh, "Randomize", tint = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.handleIntent(BackgroundsIntent.GenerateThemeFiles) },
                        enabled = !state.isGenerating
                    ) {
                        if (state.isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Download, "Export", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White
                )
            )
        }
        
        // Small preview box - top right
        AnimatedVisibility(
            visible = state.showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 72.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier.size(120.dp, 160.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.7f),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        "Preview",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.7f)
                    )
                    Spacer(Modifier.height(4.dp))
                    CreativePatternBackground(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .graphicsLayer {
                                clip = true
                                shape = RoundedCornerShape(4.dp)
                            }
                    )
                }
            }
        }
        
        // Compact scrollable controls - bottom
        AnimatedVisibility(
            visible = state.showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CompactControls(state, viewModel::handleIntent)
        }
        
        // Toggle FAB
        FloatingActionButton(
            onClick = { viewModel.handleIntent(BackgroundsIntent.ToggleControls) },
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(if (state.showControls) Icons.Filled.Close else Icons.Filled.Menu, "Toggle")
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Theme Code") },
            text = {
                SelectionContainer {
                    Text(generatedCode, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.verticalScroll(rememberScrollState()))
                }
            },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Close") } }
        )
    }
}

@Composable
private fun CreativePatternBackground(state: BackgroundsState, modifier: Modifier) {
    Box(
        modifier = modifier
            .graphicsLayer()
            .drawBehind {
                val w = size.width
                val h = size.height
                
                // Base color
                drawRect(color = state.baseColor)
                
                // Draw pattern based on type
                when (state.patternType) {
                    PatternType.SOFT_MESH -> {
                        state.blobs.forEach { blob ->
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(blob.color, state.gradientEndColor),
                                    center = Offset(w * blob.positionX, h * blob.positionY),
                                    radius = w * blob.radius * state.patternScale,
                                    tileMode = TileMode.Clamp
                                )
                            )
                        }
                    }
                    
                    PatternType.AURORA -> {
                        // Aurora effect with wavy gradients
                        for (i in 0..4) {
                            val offset = i * 0.2f
                            val colors = state.blobs.take(3).map { it.color.copy(alpha = it.alpha * state.auroraAlphaMultiplier * state.patternIntensity) }
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = colors + state.gradientEndColor,
                                    start = Offset(w * offset, 0f),
                                    end = Offset(w * (1 - offset), h),
                                    tileMode = TileMode.Mirror
                                )
                            )
                        }
                    }
                    
                    PatternType.VORONOI -> {
                        // Voronoi-style cellular pattern
                        state.geometricPoints.forEach { point ->
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        point.color.copy(alpha = state.patternIntensity * state.voronoiAlphaMultiplier),
                                        state.gradientEndColor
                                    ),
                                    center = Offset(w * point.x, h * point.y),
                                    radius = (w * 0.15f * point.size * state.patternScale)
                                ),
                                center = Offset(w * point.x, h * point.y),
                                radius = w * 0.2f * point.size * state.patternScale
                            )
                        }
                    }
                    
                    PatternType.GEOMETRIC -> {
                        // Geometric shapes with depth
                        state.geometricPoints.forEachIndexed { index, point ->
                            val angle = state.patternRotation + index * 15f
                            val rad = angle * PI.toFloat() / 180f
                            val size = w * 0.1f * point.size * state.patternScale
                            
                            drawCircle(
                                color = point.color.copy(alpha = state.patternIntensity * state.geometricAlphaMultiplier),
                                center = Offset(w * point.x + cos(rad) * 20f, h * point.y + sin(rad) * 20f),
                                radius = size
                            )
                        }
                    }
                    
                    PatternType.WAVES -> {
                        // Wave patterns
                        for (i in 0..5) {
                            val yOffset = h * i / 6f
                            val path = Path().apply {
                                moveTo(0f, yOffset)
                                for (x in 0..w.toInt() step 50) {
                                    val wave = sin((x / w.toFloat() * 4 * PI + state.patternRotation / 60f).toFloat()) * 50f * state.patternScale
                                    lineTo(x.toFloat(), yOffset + wave)
                                }
                                // Ensure we reach the right edge
                                val finalWave = sin((w / w.toFloat() * 4 * PI + state.patternRotation / 60f).toFloat()) * 50f * state.patternScale
                                lineTo(w, yOffset + finalWave)
                                // Complete the shape
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            
                            val color = state.blobs.getOrNull(i % state.blobs.size)?.color 
                                ?: state.baseColor
                            drawPath(
                                path = path,
                                color = color.copy(alpha = state.patternIntensity * state.wavesAlphaMultiplier)
                            )
                        }
                    }
                    
                    PatternType.PERLIN_NOISE -> {
                        // Simplified noise-like effect with overlapping circles
                        for (i in 0..30) {
                            val x = (i * 37 % 100) / 100f
                            val y = (i * 73 % 100) / 100f
                            val size = ((i * 13 % 50) + 30) * state.patternScale
                            val colorIndex = i % state.blobs.size
                            
                            drawCircle(
                                color = state.blobs[colorIndex].color.copy(alpha = state.patternIntensity * state.perlinNoiseAlphaMultiplier),
                                center = Offset(w * x, h * y),
                                radius = size
                            )
                        }
                    }
                }
                
                // Vignette
                if (state.vignetteEnabled) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = state.vignetteStrength)),
                            center = Offset(w * 0.5f, h * 0.4f),
                            radius = w * 1.2f
                        )
                    )
                }
            }
    )
}

@Composable
private fun CompactControls(state: BackgroundsState, onIntent: (BackgroundsIntent) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.Black.copy(alpha = 0.8f)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Pattern selector - 2 rows
            Text("Pattern", style = MaterialTheme.typography.labelSmall, color = Color.White)
            
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
                PatternType.entries.take(3).forEach { type ->
                    FilterChip(
                        selected = state.patternType == type,
                        onClick = { onIntent(BackgroundsIntent.SelectPatternType(type)) },
                        label = { Text(getShortName(type), style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
                PatternType.entries.drop(3).forEach { type ->
                    FilterChip(
                        selected = state.patternType == type,
                        onClick = { onIntent(BackgroundsIntent.SelectPatternType(type)) },
                        label = { Text(getShortName(type), style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
            
            // Pattern controls
            MiniSliderWithLabel("Intensity", state.patternIntensity, 
                { onIntent(BackgroundsIntent.UpdatePatternIntensity(it)) }, 0f..1f, Color(0xFFFFB74D))
            MiniSliderWithLabel("Scale", state.patternScale,
                { onIntent(BackgroundsIntent.UpdatePatternScale(it)) }, 0.5f..2f, Color(0xFF81C784))
            
            // Color controls based on pattern type
            when (state.patternType) {
                PatternType.SOFT_MESH, PatternType.AURORA, PatternType.WAVES, PatternType.PERLIN_NOISE -> {
                    // Blob controls for patterns using blob colors
                    HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        state.blobs.forEach { blob ->
                            FilterChip(
                                selected = state.selectedBlobId == blob.id,
                                onClick = { onIntent(BackgroundsIntent.SelectBlob(blob.id)) },
                                label = { Text(blob.name, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    state.selectedBlob?.let { blob ->
                        CompactColorSliders(
                            red = blob.colorRed,
                            green = blob.colorGreen,
                            blue = blob.colorBlue,
                            onRedChange = { onIntent(BackgroundsIntent.UpdateBlobColorRed(it)) },
                            onGreenChange = { onIntent(BackgroundsIntent.UpdateBlobColorGreen(it)) },
                            onBlueChange = { onIntent(BackgroundsIntent.UpdateBlobColorBlue(it)) }
                        )
                        
                        // Position controls only for Soft Mesh and Aurora
                        if (state.patternType == PatternType.SOFT_MESH || state.patternType == PatternType.AURORA) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                MiniSliderWithLabel("X", blob.positionX,
                                    { onIntent(BackgroundsIntent.UpdateBlobPositionX(it)) }, 0f..1f, 
                                    Color(0xFFFF6B6B), Modifier.weight(1f))
                                MiniSliderWithLabel("Y", blob.positionY,
                                    { onIntent(BackgroundsIntent.UpdateBlobPositionY(it)) }, 0f..1f,
                                    Color(0xFF4ECDC4), Modifier.weight(1f))
                            }
                        }
                    }
                    
                    // Gradient end color (for Soft Mesh and Aurora)
                    if (state.patternType == PatternType.SOFT_MESH || state.patternType == PatternType.AURORA) {
                        HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
                        Text("Gradient End", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        CompactColorSliders(
                            red = state.gradientEndColorRed,
                            green = state.gradientEndColorGreen,
                            blue = state.gradientEndColorBlue,
                            onRedChange = { onIntent(BackgroundsIntent.UpdateGradientEndColorRed(it)) },
                            onGreenChange = { onIntent(BackgroundsIntent.UpdateGradientEndColorGreen(it)) },
                            onBlueChange = { onIntent(BackgroundsIntent.UpdateGradientEndColorBlue(it)) }
                        )
                        MiniSliderWithLabel("Alpha", state.gradientEndColorAlpha,
                            { onIntent(BackgroundsIntent.UpdateGradientEndColorAlpha(it)) }, 0f..1f,
                            Color(0xFFB39DDB))
                    }
                    
                    // Alpha multipliers for each pattern type
                    when (state.patternType) {
                        PatternType.AURORA -> {
                            MiniSliderWithLabel("Color Alpha", state.auroraAlphaMultiplier,
                                { onIntent(BackgroundsIntent.UpdateAuroraAlphaMultiplier(it)) }, 0f..1f,
                                Color(0xFFA5D6A7))
                        }
                        PatternType.WAVES -> {
                            MiniSliderWithLabel("Wave Alpha", state.wavesAlphaMultiplier,
                                { onIntent(BackgroundsIntent.UpdateWavesAlphaMultiplier(it)) }, 0f..1f,
                                Color(0xFF90CAF9))
                        }
                        PatternType.PERLIN_NOISE -> {
                            MiniSliderWithLabel("Noise Alpha", state.perlinNoiseAlphaMultiplier,
                                { onIntent(BackgroundsIntent.UpdatePerlinNoiseAlphaMultiplier(it)) }, 0f..1f,
                                Color(0xFFCE93D8))
                        }
                        else -> {}
                    }
                }
                
                PatternType.VORONOI, PatternType.GEOMETRIC -> {
                    // HSV controls for geometric patterns
                    HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
                    Text("Pattern Colors", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    
                    MiniSliderWithLabel("Hue", state.geometricHueOffset,
                        { onIntent(BackgroundsIntent.UpdateGeometricHueOffset(it)) }, 0f..360f, 
                        Color(0xFFFF6B9D))
                    MiniSliderWithLabel("Saturation", state.geometricSaturation,
                        { onIntent(BackgroundsIntent.UpdateGeometricSaturation(it)) }, 0f..1f,
                        Color(0xFF64B5F6))
                    MiniSliderWithLabel("Brightness", state.geometricValue,
                        { onIntent(BackgroundsIntent.UpdateGeometricValue(it)) }, 0f..1f,
                        Color(0xFFFFD54F))
                    
                    // Gradient end color for Voronoi
                    if (state.patternType == PatternType.VORONOI) {
                        HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
                        Text("Gradient End", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        CompactColorSliders(
                            red = state.gradientEndColorRed,
                            green = state.gradientEndColorGreen,
                            blue = state.gradientEndColorBlue,
                            onRedChange = { onIntent(BackgroundsIntent.UpdateGradientEndColorRed(it)) },
                            onGreenChange = { onIntent(BackgroundsIntent.UpdateGradientEndColorGreen(it)) },
                            onBlueChange = { onIntent(BackgroundsIntent.UpdateGradientEndColorBlue(it)) }
                        )
                        MiniSliderWithLabel("Alpha", state.gradientEndColorAlpha,
                            { onIntent(BackgroundsIntent.UpdateGradientEndColorAlpha(it)) }, 0f..1f,
                            Color(0xFFB39DDB))
                        MiniSliderWithLabel("Point Alpha", state.voronoiAlphaMultiplier,
                            { onIntent(BackgroundsIntent.UpdateVoronoiAlphaMultiplier(it)) }, 0f..1f,
                            Color(0xFFFFAB91))
                    } else {
                        // Geometric pattern - no gradient, just alpha multiplier
                        HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
                        MiniSliderWithLabel("Shape Alpha", state.geometricAlphaMultiplier,
                            { onIntent(BackgroundsIntent.UpdateGeometricAlphaMultiplier(it)) }, 0f..1f,
                            Color(0xFFF48FB1))
                    }
                }
            }
            
            // Vignette
            HorizontalDivider(color = Color.White.copy(0.15f), thickness = 0.5.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vignette", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.weight(1f))
                Switch(
                    checked = state.vignetteEnabled,
                    onCheckedChange = { onIntent(BackgroundsIntent.ToggleVignette(it)) },
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

private fun getShortName(type: PatternType): String = when (type) {
    PatternType.SOFT_MESH -> "Mesh"
    PatternType.AURORA -> "Aurora"
    PatternType.VORONOI -> "Voronoi"
    PatternType.GEOMETRIC -> "Geo"
    PatternType.WAVES -> "Waves"
    PatternType.PERLIN_NOISE -> "Noise"
}

@Composable
private fun CompactColorSliders(
    red: Float,
    green: Float,
    blue: Float,
    onRedChange: (Float) -> Unit,
    onGreenChange: (Float) -> Unit,
    onBlueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        MiniSlider("R", red, onRedChange, Color.Red)
        MiniSlider("G", green, onGreenChange, Color.Green)
        MiniSlider("B", blue, onBlueChange, Color.Blue)
    }
}

@Composable
private fun MiniSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(0.9f), modifier = Modifier.width(10.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f).height(20.dp),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color.copy(0.7f),
                inactiveTrackColor = Color.Gray.copy(0.3f)
            )
        )
        Text((value * 255).toInt().toString(), 
            style = MaterialTheme.typography.labelSmall, 
            color = Color.White.copy(0.6f),
            modifier = Modifier.width(24.dp))
    }
}

@Composable
private fun MiniSliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(0.9f), modifier = Modifier.width(50.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color.copy(0.7f),
                inactiveTrackColor = Color.Gray.copy(0.3f)
            ),
            modifier = Modifier.weight(1f).height(20.dp)
        )
        Text("%.1f".format(value), 
            style = MaterialTheme.typography.labelSmall, 
            color = Color.White.copy(0.6f),
            modifier = Modifier.width(28.dp))
    }
}

@Composable
private fun CompactSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(0.9f))
            Text("%.2f".format(value), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.7f))
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color.copy(0.7f),
                inactiveTrackColor = Color.Gray.copy(0.3f)
            ),
            modifier = Modifier.height(32.dp)
        )
    }
}
