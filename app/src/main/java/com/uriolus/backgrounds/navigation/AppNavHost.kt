package com.uriolus.backgrounds.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uriolus.core.navigation.NavDestination
import com.uriolus.feature.backgrounds.BackgroundsScreen
import com.uriolus.feature.shaders.ShadersScreen
import com.uriolus.feature.shaders.ShaderHelpScreen

/**
 * Navigation host for the app
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home,
        modifier = modifier
    ) {
        composable<NavDestination.Home> {
            HomeScreen()
        }
        
        composable<NavDestination.Backgrounds> {
            BackgroundsScreen()
        }
        
        composable<NavDestination.Shaders> {
            ShadersScreen(
                onNavigateToHelp = { navController.navigate(NavDestination.ShaderHelp) }
            )
        }
        
        composable<NavDestination.ShaderHelp> {
            ShaderHelpScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

@Composable
private fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to Compose Features!\n\nOpen the drawer to navigate.",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}
