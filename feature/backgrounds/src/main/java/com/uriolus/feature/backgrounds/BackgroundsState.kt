package com.uriolus.feature.backgrounds

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * UI State for creative background designer
 */
data class BackgroundsState(
    // Pattern type
    val patternType: PatternType = PatternType.SOFT_MESH,
    
    // Base background color
    val baseColorRed: Float = 0.95f,
    val baseColorGreen: Float = 0.95f,
    val baseColorBlue: Float = 0.98f,
    
    // Gradient blobs (for mesh patterns)
    val blobs: List<GradientBlob> = getDefaultBlobs(),
    
    // Geometric shapes (for voronoi/geometric patterns)
    val geometricPoints: List<GeometricPoint> = getDefaultGeometricPoints(),
    
    // Pattern-specific properties
    val patternIntensity: Float = 0.8f,
    val patternScale: Float = 1.0f,
    val patternRotation: Float = 0f,
    
    // Geometric pattern HSV controls
    val geometricHueOffset: Float = 0f,      // 0-360 degrees
    val geometricSaturation: Float = 0.7f,   // 0-1
    val geometricValue: Float = 0.9f,        // 0-1 (brightness)
    
    // Gradient end color (for Soft Mesh, Aurora, Voronoi)
    val gradientEndColorRed: Float = 0f,
    val gradientEndColorGreen: Float = 0f,
    val gradientEndColorBlue: Float = 0f,
    val gradientEndColorAlpha: Float = 0f,   // Default transparent
    
    // Pattern-specific alpha multipliers
    val auroraAlphaMultiplier: Float = 0.6f,
    val voronoiAlphaMultiplier: Float = 0.8f,
    val geometricAlphaMultiplier: Float = 0.7f,
    val wavesAlphaMultiplier: Float = 0.3f,
    val perlinNoiseAlphaMultiplier: Float = 0.4f,
    
    // Vignette effect
    val vignetteEnabled: Boolean = true,
    val vignetteStrength: Float = 0.2f,
    
    // UI state
    val showControls: Boolean = true,
    val selectedBlobId: Int = 1,
    val isGenerating: Boolean = false
) {
    val baseColor: Color
        get() = Color(baseColorRed, baseColorGreen, baseColorBlue)
    
    val gradientEndColor: Color
        get() = Color(gradientEndColorRed, gradientEndColorGreen, gradientEndColorBlue, gradientEndColorAlpha)
    
    val selectedBlob: GradientBlob?
        get() = blobs.find { it.id == selectedBlobId }
}

data class GradientBlob(
    val id: Int,
    val name: String,
    val colorRed: Float,
    val colorGreen: Float,
    val colorBlue: Float,
    val alpha: Float,
    val positionX: Float,
    val positionY: Float,
    val radius: Float
) {
    val color: Color
        get() = Color(colorRed, colorGreen, colorBlue, alpha)
}

data class GeometricPoint(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float = 1f
)

enum class PatternType(val displayName: String) {
    SOFT_MESH("Soft Mesh"),
    AURORA("Aurora"),
    VORONOI("Voronoi"),
    GEOMETRIC("Geometric"),
    WAVES("Waves"),
    PERLIN_NOISE("Perlin Noise")
}

private fun getDefaultBlobs() = listOf(
    GradientBlob(
        id = 1,
        name = "Teal",
        colorRed = 0.4f,
        colorGreen = 0.8f,
        colorBlue = 0.8f,
        alpha = 0.75f,
        positionX = 0.1f,
        positionY = 0.2f,
        radius = 0.9f
    ),
    GradientBlob(
        id = 2,
        name = "Lavender",
        colorRed = 0.7f,
        colorGreen = 0.6f,
        colorBlue = 0.9f,
        alpha = 0.8f,
        positionX = 0.85f,
        positionY = 0.35f,
        radius = 0.9f
    ),
    GradientBlob(
        id = 3,
        name = "Peach",
        colorRed = 1.0f,
        colorGreen = 0.8f,
        colorBlue = 0.7f,
        alpha = 0.8f,
        positionX = 0.25f,
        positionY = 0.9f,
        radius = 1.0f
    )
)

private fun getDefaultGeometricPoints(hueOffset: Float = 0f, saturation: Float = 0.7f, value: Float = 0.9f) = List(20) { index ->
    val angle = (index / 20f) * 2 * Math.PI.toFloat()
    val radius = 0.3f + (index % 5) * 0.1f
    GeometricPoint(
        x = 0.5f + radius * kotlin.math.cos(angle),
        y = 0.5f + radius * kotlin.math.sin(angle),
        color = Color.hsv((index * 18f + hueOffset) % 360f, saturation, value),
        size = 0.5f + (index % 3) * 0.3f
    )
}

fun randomizeBlobs(): List<GradientBlob> {
    val colors = listOf(
        Triple(0.4f, 0.8f, 0.9f), // Cyan
        Triple(0.9f, 0.5f, 0.7f), // Pink
        Triple(0.7f, 0.9f, 0.4f), // Lime
        Triple(0.9f, 0.7f, 0.3f), // Orange
        Triple(0.6f, 0.4f, 0.9f), // Purple
        Triple(0.9f, 0.9f, 0.4f), // Yellow
    )
    
    return List(3 + Random.nextInt(2)) { index ->
        val color = colors.random()
        GradientBlob(
            id = index + 1,
            name = "Blob${index + 1}",
            colorRed = color.first,
            colorGreen = color.second,
            colorBlue = color.third,
            alpha = 0.6f + Random.nextFloat() * 0.3f,
            positionX = Random.nextFloat(),
            positionY = Random.nextFloat(),
            radius = 0.6f + Random.nextFloat() * 0.8f
        )
    }
}

fun randomizeGeometricPoints(hueOffset: Float = 0f, saturation: Float = 0.7f, value: Float = 0.9f): List<GeometricPoint> {
    return List(15 + Random.nextInt(25)) {
        GeometricPoint(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            color = Color.hsv((Random.nextFloat() * 360f + hueOffset) % 360f, saturation, value),
            size = 0.3f + Random.nextFloat() * 0.7f
        )
    }
}
