package com.uriolus.feature.backgrounds

import androidx.compose.ui.graphics.Color
import com.uriolus.core.common.mvi.MviViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * ViewModel for creative background designer following MVI pattern
 */
class BackgroundsViewModel : MviViewModel<BackgroundsState, BackgroundsIntent, BackgroundsEvent>(
    initialState = BackgroundsState()
) {

    override fun handleIntent(intent: BackgroundsIntent) {
        when (intent) {
            // Pattern type
            is BackgroundsIntent.SelectPatternType -> 
                updateState { copy(patternType = intent.type) }
            
            // Base color updates
            is BackgroundsIntent.UpdateBaseColorRed -> 
                updateState { copy(baseColorRed = intent.value) }
            is BackgroundsIntent.UpdateBaseColorGreen -> 
                updateState { copy(baseColorGreen = intent.value) }
            is BackgroundsIntent.UpdateBaseColorBlue -> 
                updateState { copy(baseColorBlue = intent.value) }
            
            // Pattern properties
            is BackgroundsIntent.UpdatePatternIntensity ->
                updateState { copy(patternIntensity = intent.value) }
            is BackgroundsIntent.UpdatePatternScale ->
                updateState { copy(patternScale = intent.value) }
            is BackgroundsIntent.UpdatePatternRotation ->
                updateState { copy(patternRotation = intent.value) }
            
            // Geometric pattern HSV controls
            is BackgroundsIntent.UpdateGeometricHueOffset -> {
                updateState { 
                    copy(
                        geometricHueOffset = intent.value,
                        geometricPoints = randomizeGeometricPoints(intent.value, geometricSaturation, geometricValue)
                    )
                }
            }
            is BackgroundsIntent.UpdateGeometricSaturation -> {
                updateState { 
                    copy(
                        geometricSaturation = intent.value,
                        geometricPoints = randomizeGeometricPoints(geometricHueOffset, intent.value, geometricValue)
                    )
                }
            }
            is BackgroundsIntent.UpdateGeometricValue -> {
                updateState { 
                    copy(
                        geometricValue = intent.value,
                        geometricPoints = randomizeGeometricPoints(geometricHueOffset, geometricSaturation, intent.value)
                    )
                }
            }
            is BackgroundsIntent.RegenerateGeometricPoints -> {
                updateState { 
                    copy(geometricPoints = randomizeGeometricPoints(geometricHueOffset, geometricSaturation, geometricValue))
                }
            }
            
            // Gradient end color controls
            is BackgroundsIntent.UpdateGradientEndColorRed ->
                updateState { copy(gradientEndColorRed = intent.value) }
            is BackgroundsIntent.UpdateGradientEndColorGreen ->
                updateState { copy(gradientEndColorGreen = intent.value) }
            is BackgroundsIntent.UpdateGradientEndColorBlue ->
                updateState { copy(gradientEndColorBlue = intent.value) }
            is BackgroundsIntent.UpdateGradientEndColorAlpha ->
                updateState { copy(gradientEndColorAlpha = intent.value) }
            
            // Alpha multiplier controls
            is BackgroundsIntent.UpdateAuroraAlphaMultiplier ->
                updateState { copy(auroraAlphaMultiplier = intent.value) }
            is BackgroundsIntent.UpdateVoronoiAlphaMultiplier ->
                updateState { copy(voronoiAlphaMultiplier = intent.value) }
            is BackgroundsIntent.UpdateGeometricAlphaMultiplier ->
                updateState { copy(geometricAlphaMultiplier = intent.value) }
            is BackgroundsIntent.UpdateWavesAlphaMultiplier ->
                updateState { copy(wavesAlphaMultiplier = intent.value) }
            is BackgroundsIntent.UpdatePerlinNoiseAlphaMultiplier ->
                updateState { copy(perlinNoiseAlphaMultiplier = intent.value) }
            
            // Blob selection
            is BackgroundsIntent.SelectBlob -> 
                updateState { copy(selectedBlobId = intent.blobId) }
            
            // Selected blob updates
            is BackgroundsIntent.UpdateBlobColorRed -> updateSelectedBlob { copy(colorRed = intent.value) }
            is BackgroundsIntent.UpdateBlobColorGreen -> updateSelectedBlob { copy(colorGreen = intent.value) }
            is BackgroundsIntent.UpdateBlobColorBlue -> updateSelectedBlob { copy(colorBlue = intent.value) }
            is BackgroundsIntent.UpdateBlobAlpha -> updateSelectedBlob { copy(alpha = intent.value) }
            is BackgroundsIntent.UpdateBlobPositionX -> updateSelectedBlob { copy(positionX = intent.value) }
            is BackgroundsIntent.UpdateBlobPositionY -> updateSelectedBlob { copy(positionY = intent.value) }
            is BackgroundsIntent.UpdateBlobRadius -> updateSelectedBlob { copy(radius = intent.value) }
            
            // Vignette updates
            is BackgroundsIntent.ToggleVignette -> 
                updateState { copy(vignetteEnabled = intent.enabled) }
            is BackgroundsIntent.UpdateVignetteStrength -> 
                updateState { copy(vignetteStrength = intent.value) }
            
            // Randomization
            is BackgroundsIntent.RandomizePattern -> randomize()
            
            // UI updates
            is BackgroundsIntent.ToggleControls -> 
                updateState { copy(showControls = !showControls) }
            is BackgroundsIntent.GenerateThemeFiles -> 
                generateThemeFiles()
        }
    }
    
    private fun updateSelectedBlob(transform: GradientBlob.() -> GradientBlob) {
        updateState {
            val updatedBlobs = blobs.map { blob ->
                if (blob.id == selectedBlobId) blob.transform() else blob
            }
            copy(blobs = updatedBlobs)
        }
    }
    
    private fun randomize() {
        val randomPattern = PatternType.entries.random()
        val randomBase = Color.hsv(Random.nextFloat() * 360f, 0.1f, 0.9f + Random.nextFloat() * 0.1f)
        
        updateState {
            copy(
                patternType = randomPattern,
                baseColorRed = randomBase.red,
                baseColorGreen = randomBase.green,
                baseColorBlue = randomBase.blue,
                blobs = randomizeBlobs(),
                geometricPoints = randomizeGeometricPoints(
                    Random.nextFloat() * 360f,
                    0.6f + Random.nextFloat() * 0.3f,
                    0.8f + Random.nextFloat() * 0.2f
                ),
                patternIntensity = 0.6f + Random.nextFloat() * 0.3f,
                patternScale = 0.7f + Random.nextFloat() * 0.6f,
                patternRotation = Random.nextFloat() * 360f,
                vignetteEnabled = Random.nextBoolean(),
                vignetteStrength = if (Random.nextBoolean()) 0.1f + Random.nextFloat() * 0.3f else 0.2f
            )
        }
        
        sendEvent(BackgroundsEvent.ShowMessage("Generated ${randomPattern.displayName} pattern!"))
    }

    private fun generateThemeFiles() {
        val currentState = state.value
        
        updateState { copy(isGenerating = true) }
        
        try {
            val themeCode = buildString {
                // Header
                appendLine("// Generated ${currentState.patternType.displayName} Background")
                appendLine("// Copy this entire code block to your project")
                appendLine()
                
                // Imports
                appendLine("import androidx.compose.foundation.layout.Box")
                appendLine("import androidx.compose.foundation.layout.fillMaxSize")
                appendLine("import androidx.compose.runtime.Composable")
                appendLine("import androidx.compose.ui.Modifier")
                appendLine("import androidx.compose.ui.draw.drawBehind")
                appendLine("import androidx.compose.ui.geometry.Offset")
                appendLine("import androidx.compose.ui.graphics.Brush")
                appendLine("import androidx.compose.ui.graphics.Color")
                appendLine("import androidx.compose.ui.graphics.Path")
                appendLine("import androidx.compose.ui.graphics.TileMode")
                appendLine("import androidx.compose.ui.graphics.graphicsLayer")
                appendLine("import kotlin.math.PI")
                appendLine("import kotlin.math.cos")
                appendLine("import kotlin.math.sin")
                appendLine()
                
                // Main composable
                appendLine("@Composable")
                appendLine("fun CreativeBackground(modifier: Modifier = Modifier) {")
                appendLine("    Box(")
                appendLine("        modifier = modifier")
                appendLine("            .fillMaxSize()")
                appendLine("            .graphicsLayer()")
                appendLine("            .drawBehind {")
                appendLine("                val w = size.width")
                appendLine("                val h = size.height")
                appendLine()
                appendLine("                // Base color")
                appendLine("                drawRect(color = Color(${currentState.baseColorRed}f, ${currentState.baseColorGreen}f, ${currentState.baseColorBlue}f))")
                appendLine()
                
                // Pattern-specific drawing code
                when (currentState.patternType) {
                    PatternType.SOFT_MESH -> {
                        appendLine("                // Soft Mesh Pattern")
                        currentState.blobs.forEach { blob ->
                            appendLine("                drawRect(")
                            appendLine("                    brush = Brush.radialGradient(")
                            appendLine("                        colors = listOf(")
                            appendLine("                            Color(${blob.colorRed}f, ${blob.colorGreen}f, ${blob.colorBlue}f, ${blob.alpha}f),")
                            appendLine("                            Color.Transparent")
                            appendLine("                        ),")
                            appendLine("                        center = Offset(w * ${blob.positionX}f, h * ${blob.positionY}f),")
                            appendLine("                        radius = w * ${blob.radius}f * ${currentState.patternScale}f,")
                            appendLine("                        tileMode = TileMode.Clamp")
                            appendLine("                    )")
                            appendLine("                )")
                        }
                    }
                    
                    PatternType.AURORA -> {
                        appendLine("                // Aurora Pattern")
                        appendLine("                for (i in 0..4) {")
                        appendLine("                    val offset = i * 0.2f")
                        val colors = currentState.blobs.take(3)
                        appendLine("                    val colors = listOf(")
                        colors.forEach { blob ->
                            val adjustedAlpha = blob.alpha * 0.6f * currentState.patternIntensity
                            appendLine("                        Color(${blob.colorRed}f, ${blob.colorGreen}f, ${blob.colorBlue}f, ${adjustedAlpha}f),")
                        }
                        appendLine("                        Color.Transparent")
                        appendLine("                    )")
                        appendLine("                    drawRect(")
                        appendLine("                        brush = Brush.linearGradient(")
                        appendLine("                            colors = colors,")
                        appendLine("                            start = Offset(w * offset, 0f),")
                        appendLine("                            end = Offset(w * (1 - offset), h),")
                        appendLine("                            tileMode = TileMode.Mirror")
                        appendLine("                        )")
                        appendLine("                    )")
                        appendLine("                }")
                    }
                    
                    PatternType.VORONOI -> {
                        appendLine("                // Voronoi Pattern")
                        currentState.geometricPoints.forEach { point ->
                            appendLine("                drawCircle(")
                            appendLine("                    brush = Brush.radialGradient(")
                            appendLine("                        colors = listOf(")
                            appendLine("                            Color(${point.color.red}f, ${point.color.green}f, ${point.color.blue}f, ${currentState.patternIntensity * 0.8f}f),")
                            appendLine("                            Color.Transparent")
                            appendLine("                        ),")
                            appendLine("                        center = Offset(w * ${point.x}f, h * ${point.y}f),")
                            appendLine("                        radius = w * 0.15f * ${point.size}f * ${currentState.patternScale}f")
                            appendLine("                    ),")
                            appendLine("                    center = Offset(w * ${point.x}f, h * ${point.y}f),")
                            appendLine("                    radius = w * 0.2f * ${point.size}f * ${currentState.patternScale}f")
                            appendLine("                )")
                        }
                    }
                    
                    PatternType.GEOMETRIC -> {
                        appendLine("                // Geometric Pattern")
                        currentState.geometricPoints.forEachIndexed { index, point ->
                            val angle = currentState.patternRotation + index * 15f
                            val rad = angle * PI.toFloat() / 180f
                            val offsetX = cos(rad) * 20f
                            val offsetY = sin(rad) * 20f
                            appendLine("                drawCircle(")
                            appendLine("                    color = Color(${point.color.red}f, ${point.color.green}f, ${point.color.blue}f, ${currentState.patternIntensity * 0.7f}f),")
                            appendLine("                    center = Offset(w * ${point.x}f + ${offsetX}f, h * ${point.y}f + ${offsetY}f),")
                            appendLine("                    radius = w * 0.1f * ${point.size}f * ${currentState.patternScale}f")
                            appendLine("                )")
                        }
                    }
                    
                    PatternType.WAVES -> {
                        appendLine("                // Wave Pattern")
                        appendLine("                for (i in 0..5) {")
                        appendLine("                    val yOffset = h * i / 6f")
                        appendLine("                    val path = Path().apply {")
                        appendLine("                        moveTo(0f, yOffset)")
                        appendLine("                        for (x in 0..w.toInt() step 50) {")
                        appendLine("                            val wave = sin((x / w.toFloat() * 4 * PI + ${currentState.patternRotation}f / 60f).toFloat()) * 50f * ${currentState.patternScale}f")
                        appendLine("                            lineTo(x.toFloat(), yOffset + wave)")
                        appendLine("                        }")
                        appendLine("                        val finalWave = sin((w / w.toFloat() * 4 * PI + ${currentState.patternRotation}f / 60f).toFloat()) * 50f * ${currentState.patternScale}f")
                        appendLine("                        lineTo(w, yOffset + finalWave)")
                        appendLine("                        lineTo(w, h)")
                        appendLine("                        lineTo(0f, h)")
                        appendLine("                        close()")
                        appendLine("                    }")
                        appendLine()
                        currentState.blobs.forEachIndexed { index, blob ->
                            appendLine("                    val color${index + 1} = Color(${blob.colorRed}f, ${blob.colorGreen}f, ${blob.colorBlue}f, ${currentState.patternIntensity * 0.3f}f)")
                        }
                        appendLine("                    val colors = listOf(${currentState.blobs.indices.joinToString(", ") { "color${it + 1}" }})")
                        appendLine("                    drawPath(")
                        appendLine("                        path = path,")
                        appendLine("                        color = colors[i % colors.size]")
                        appendLine("                    )")
                        appendLine("                }")
                    }
                    
                    PatternType.PERLIN_NOISE -> {
                        appendLine("                // Perlin Noise Pattern")
                        appendLine("                for (i in 0..30) {")
                        appendLine("                    val x = (i * 37 % 100) / 100f")
                        appendLine("                    val y = (i * 73 % 100) / 100f")
                        appendLine("                    val size = ((i * 13 % 50) + 30) * ${currentState.patternScale}f")
                        currentState.blobs.forEachIndexed { index, blob ->
                            appendLine("                    val color${index} = Color(${blob.colorRed}f, ${blob.colorGreen}f, ${blob.colorBlue}f, ${currentState.patternIntensity * 0.4f}f)")
                        }
                        appendLine("                    val colors = listOf(${currentState.blobs.indices.joinToString(", ") { "color$it" }})")
                        appendLine("                    drawCircle(")
                        appendLine("                        color = colors[i % colors.size],")
                        appendLine("                        center = Offset(w * x, h * y),")
                        appendLine("                        radius = size")
                        appendLine("                    )")
                        appendLine("                }")
                    }
                }
                
                appendLine()
                
                // Vignette
                if (currentState.vignetteEnabled) {
                    appendLine("                // Vignette")
                    appendLine("                drawRect(")
                    appendLine("                    brush = Brush.radialGradient(")
                    appendLine("                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = ${currentState.vignetteStrength}f)),")
                    appendLine("                        center = Offset(w * 0.5f, h * 0.4f),")
                    appendLine("                        radius = w * 1.2f")
                    appendLine("                    )")
                    appendLine("                )")
                }
                
                appendLine("            }")
                appendLine("    )")
                appendLine("}")
                appendLine()
                appendLine("// Configuration:")
                appendLine("// Pattern: ${currentState.patternType.displayName}")
                appendLine("// Intensity: ${currentState.patternIntensity}")
                appendLine("// Scale: ${currentState.patternScale}")
                if (currentState.patternType == PatternType.GEOMETRIC || currentState.patternType == PatternType.WAVES) {
                    appendLine("// Rotation: ${currentState.patternRotation}")
                }
                appendLine("// Vignette: ${if (currentState.vignetteEnabled) "Enabled (${currentState.vignetteStrength})" else "Disabled"}")
            }
            
            sendEvent(BackgroundsEvent.ThemeFilesGenerated(themeCode))
            sendEvent(BackgroundsEvent.ShowMessage("Theme code generated!"))
            
        } catch (e: Exception) {
            sendEvent(BackgroundsEvent.GenerationError(e.message ?: "Unknown error"))
        } finally {
            updateState { copy(isGenerating = false) }
        }
    }
    
    private fun colorToHex(color: Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return String.format("FF%02X%02X%02X", red, green, blue)
    }
}
