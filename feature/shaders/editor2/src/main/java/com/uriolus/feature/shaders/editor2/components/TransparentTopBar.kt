package com.uriolus.feature.shaders.editor2.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Transparent top bar that shows the shader background behind it
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentTopBar(
    title: String,
    alpha: Float = 0.3f,
    onNavigateBack: () -> Unit,
    onSaveClick: () -> Unit,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onLoadClick) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Load shader",
                    tint = Color.White
                )
            }
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save shader",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = alpha)
        ),
        modifier = modifier
    )
}
