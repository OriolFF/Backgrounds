package com.uriolus.backgrounds.navigation

import androidx.compose.foundation.background
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
import com.uriolus.feature.shaders.AttributionsScreen
import com.uriolus.feature.shaders.editor2.Editor2Screen

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
        
        composable<NavDestination.Editor2> {
            Editor2Screen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToHelp = { navController.navigate(NavDestination.ShaderHelp) }
            )
        }
        
        composable<NavDestination.ShaderHelp> {
            ShaderHelpScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable<NavDestination.Attributions> {
            AttributionsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

@Composable
private fun HomeScreen() {
    // Load plasma shader code
    val shaderCode = """
        uniform float2 iResolution;
        uniform float iTime;
        
        half4 main(float2 fragCoord) {
            float2 uv = fragCoord / iResolution;
            
            float t = iTime * 0.5;
            
            float v1 = sin(uv.x * 10.0 + t);
            float v2 = sin(uv.y * 10.0 + t);
            float v3 = sin((uv.x + uv.y) * 10.0 + t);
            float v4 = sin(sqrt(uv.x * uv.x + uv.y * uv.y) * 10.0 + t);
            
            float composite = (v1 + v2 + v3 + v4) * 0.25;
            
            float r = 0.5 + 0.5 * sin(composite * 3.14159);
            float g = 0.5 + 0.5 * sin(composite * 3.14159 + 2.0);
            float b = 0.5 + 0.5 * sin(composite * 3.14159 + 4.0);
            
            return half4(r, g, b, 1.0);
        }
    """.trimIndent()
    
    com.uriolus.core.ui.shader.ShaderBackground(
        shaderCode = shaderCode,
        modifier = Modifier.fillMaxSize()
    ) {
        // Semi-transparent overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f),
                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Welcome to Backgrounds!\n\nOpen the drawer to navigate.",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
