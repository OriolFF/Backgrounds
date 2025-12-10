package com.uriolus.core.navigation

import kotlinx.serialization.Serializable

/**
 * Sealed class defining all navigation destinations in the app
 * Using Kotlin Serialization for type-safe navigation
 */
sealed interface NavDestination {
    
    @Serializable
    data object Home : NavDestination
    
    @Serializable
    data object Backgrounds : NavDestination
    
    @Serializable
    data object Shaders : NavDestination
}
