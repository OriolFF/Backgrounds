package com.uriolus.feature.backgrounds

/**
 * User intents/actions for creative background designer
 */
sealed interface BackgroundsIntent {
    // Pattern type selection
    data class SelectPatternType(val type: PatternType) : BackgroundsIntent
    
    // Base color controls
    data class UpdateBaseColorRed(val value: Float) : BackgroundsIntent
    data class UpdateBaseColorGreen(val value: Float) : BackgroundsIntent
    data class UpdateBaseColorBlue(val value: Float) : BackgroundsIntent
    
    // Pattern properties
    data class UpdatePatternIntensity(val value: Float) : BackgroundsIntent
    data class UpdatePatternScale(val value: Float) : BackgroundsIntent
    data class UpdatePatternRotation(val value: Float) : BackgroundsIntent
    
    // Geometric pattern HSV controls
    data class UpdateGeometricHueOffset(val value: Float) : BackgroundsIntent
    data class UpdateGeometricSaturation(val value: Float) : BackgroundsIntent
    data class UpdateGeometricValue(val value: Float) : BackgroundsIntent
    data object RegenerateGeometricPoints : BackgroundsIntent
    
    // Gradient end color controls
    data class UpdateGradientEndColorRed(val value: Float) : BackgroundsIntent
    data class UpdateGradientEndColorGreen(val value: Float) : BackgroundsIntent
    data class UpdateGradientEndColorBlue(val value: Float) : BackgroundsIntent
    data class UpdateGradientEndColorAlpha(val value: Float) : BackgroundsIntent
    
    // Alpha multiplier controls
    data class UpdateAuroraAlphaMultiplier(val value: Float) : BackgroundsIntent
    data class UpdateVoronoiAlphaMultiplier(val value: Float) : BackgroundsIntent
    data class UpdateGeometricAlphaMultiplier(val value: Float) : BackgroundsIntent
    data class UpdateWavesAlphaMultiplier(val value: Float) : BackgroundsIntent
    data class UpdatePerlinNoiseAlphaMultiplier(val value: Float) : BackgroundsIntent
    
    // Blob selection and controls
    data class SelectBlob(val blobId: Int) : BackgroundsIntent
    data class UpdateBlobColorRed(val value: Float) : BackgroundsIntent
    data class UpdateBlobColorGreen(val value: Float) : BackgroundsIntent
    data class UpdateBlobColorBlue(val value: Float) : BackgroundsIntent
    data class UpdateBlobAlpha(val value: Float) : BackgroundsIntent
    data class UpdateBlobPositionX(val value: Float) : BackgroundsIntent
    data class UpdateBlobPositionY(val value: Float) : BackgroundsIntent
    data class UpdateBlobRadius(val value: Float) : BackgroundsIntent
    
    // Vignette controls
    data class ToggleVignette(val enabled: Boolean) : BackgroundsIntent
    data class UpdateVignetteStrength(val value: Float) : BackgroundsIntent
    
    // Randomization
    data object RandomizePattern : BackgroundsIntent
    
    // UI controls
    data object ToggleControls : BackgroundsIntent
    data object GenerateThemeFiles : BackgroundsIntent
}
