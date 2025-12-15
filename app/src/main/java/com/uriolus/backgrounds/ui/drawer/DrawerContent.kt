package com.uriolus.backgrounds.ui.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.uriolus.core.navigation.NavDestination

/**
 * Drawer menu content
 */
@Composable
fun DrawerContent(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Spacer(Modifier.height(16.dp))
        
        // Header
        DrawerHeader()
        
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        
        // Menu Items
        DrawerMenuItem(
            icon = Icons.Default.Home,
            label = "Home",
            isSelected = currentDestination is NavDestination.Home,
            onClick = {
                onNavigate(NavDestination.Home)
                onCloseDrawer()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Settings,
            label = "Backgrounds",
            isSelected = currentDestination is NavDestination.Backgrounds,
            onClick = {
                onNavigate(NavDestination.Backgrounds)
                onCloseDrawer()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Edit,
            label = "Shader Editor",
            isSelected = currentDestination is NavDestination.Shaders,
            onClick = {
                onNavigate(NavDestination.Shaders)
                onCloseDrawer()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Code,
            label = "Shader Editor 2",
            isSelected = currentDestination is NavDestination.Editor2,
            onClick = {
                onNavigate(NavDestination.Editor2)
                onCloseDrawer()
            }
        )
        
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.Info,
            label = "Attributions",
            isSelected = currentDestination is NavDestination.Attributions,
            onClick = {
                onNavigate(NavDestination.Attributions)
                onCloseDrawer()
            }
        )
    }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Compose Features",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Exploring Jetpack Compose",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}
