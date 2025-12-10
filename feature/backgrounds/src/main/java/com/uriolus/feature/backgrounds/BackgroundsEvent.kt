package com.uriolus.feature.backgrounds

/**
 * One-time events for background designer
 */
sealed interface BackgroundsEvent {
    data class ShowMessage(val message: String) : BackgroundsEvent
    data class ThemeFilesGenerated(val filePath: String) : BackgroundsEvent
    data class GenerationError(val error: String) : BackgroundsEvent
}
