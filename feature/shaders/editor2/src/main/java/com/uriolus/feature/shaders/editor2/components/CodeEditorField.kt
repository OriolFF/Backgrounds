package com.uriolus.feature.shaders.editor2.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Code-friendly text editor field with monospace font
 * and proper keyboard configuration for shader code
 */
@Composable
fun CodeEditorField(
    code: String,
    onCodeChange: (String, Int) -> Unit,
    alpha: Float = 0.85f,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(code)) }
    
    // Update text field value when code changes externally (not from user input)
    LaunchedEffect(code) {
        if (textFieldValue.text != code) {
            // Code changed externally, update but try to preserve cursor position
            val currentSelection = textFieldValue.selection
            val newStart = currentSelection.start.coerceIn(0, code.length)
            val newEnd = currentSelection.end.coerceIn(0, code.length)
            textFieldValue = TextFieldValue(
                text = code,
                selection = TextRange(newStart, newEnd)
            )
        }
    }
    
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = alpha))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Line numbers
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top
            ) {
                val lineCount = code.count { it == '\n' } + 1
                for (lineNumber in 1..lineCount) {
                    Text(
                        text = lineNumber.toString().padStart(3, ' '),
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    )
                }
            }
            
            // Code editor
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onCodeChange(newValue.text, newValue.selection.start)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = Color(0xFFE0E0E0),
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Ascii
                )
            )
        }
    }
}
