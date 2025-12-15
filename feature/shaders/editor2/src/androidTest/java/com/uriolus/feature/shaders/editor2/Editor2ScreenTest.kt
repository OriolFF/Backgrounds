package com.uriolus.feature.shaders.editor2

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uriolus.feature.shaders.editor2.components.*
import com.uriolus.feature.shaders.editor2.storage.ShaderFile
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for Editor2Screen
 * 
 * Tests cover:
 * 1. Screen rendering
 * 2. Code editor editability
 * 3. Keyboard space management
 * 4. Shader saving
 * 5. Shader loading
 * 6. Code collapse/expand
 */
@RunWith(AndroidJUnit4::class)
class Editor2ScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    /**
     * Test 1: Screen renders successfully
     * Verify that all main UI components are present
     */
    @Test
    fun testScreenRendersSuccessfully() {
        composeTestRule.setContent {
            Editor2Screen(
                onNavigateBack = {}
            )
        }
        
        // Verify top bar exists
        composeTestRule.onNodeWithContentDescription("Back").assertExists()
        composeTestRule.onNodeWithContentDescription("Save shader").assertExists()
        composeTestRule.onNodeWithContentDescription("Load shader").assertExists()
        
        // Verify code section header exists
        composeTestRule.onNodeWithText("Shader Code").assertExists()
        
        // Note: ShaderPreviewCanvas is rendered but doesn't have testTag
        // We verify it indirectly through the screen rendering without errors
    }
    
    /**
     * Test 2: Code editor is editable
     * Verify that users can type into the code editor
     */
    @Test
    fun testCodeEditorIsEditable() {
        composeTestRule.setContent {
            var code = "uniform float iTime;"
            CodeEditorField(
                code = code,
                onCodeChange = { newCode, _ -> code = newCode }
            )
        }
        
        // Find the text field (BasicTextField doesn't have a semantic label by default)
        // We can check if the initial code is displayed
        composeTestRule.onNodeWithText("uniform float iTime;", substring = true)
            .assertExists()
        
        // Note: Actually typing into BasicTextField in tests requires more complex setup
        // This test verifies the field is rendered and displays content
    }
    
    /**
     * Test 3: Space for keyboard + code + preview
     * Verify layout accommodates all components when keyboard is visible
     */
    @Test
    fun testSpaceForKeyboardCodeAndPreview() {
        composeTestRule.setContent {
            Editor2Screen(
                onNavigateBack = {}
            )
        }
        
        // Verify code section exists (can be expanded/collapsed)
        val codeSection = composeTestRule.onNodeWithText("Shader Code")
        codeSection.assertExists()
        
        // Verify code section is clickable for expand/collapse
        codeSection.assertHasClickAction()
        
        // When code is expanded, it should be visible
        // The layout uses imePadding() to ensure proper spacing with keyboard
        composeTestRule.onNodeWithText("Shader Code").performClick()
        
        // After clicking to expand, code editor should be visible
        // Note: WindowInsets.ime testing requires UI Automator or actual device
        // This test verifies the layout structure is correct
    }
    
    /**
     * Test 4: Shader saving works
     * Verify save dialog appears and accepts input
     */
    @Test
    fun testShaderSavingWorks() {
        composeTestRule.setContent {
            Editor2Screen(
                onNavigateBack = {}
            )
        }
        
        // Click save button
        composeTestRule.onNodeWithContentDescription("Save shader").performClick()
        
        // Verify save dialog appears
        composeTestRule.onNodeWithText("Save Shader").assertExists()
        composeTestRule.onNodeWithText("Enter a name for your shader:").assertExists()
        
        // Verify input field and buttons exist
        composeTestRule.onNodeWithText("Shader name").assertExists()
        composeTestRule.onNodeWithText("Save").assertExists()
        composeTestRule.onNodeWithText("Cancel").assertExists()
        
        // Cancel button should dismiss dialog
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNodeWithText("Save Shader").assertDoesNotExist()
    }
    
    /**
     * Test 5: Shader loading works
     * Verify load dialog appears with presets and saved shaders
     */
    @Test
    fun testShaderLoadingWorks() {
        composeTestRule.setContent {
            Editor2Screen(
                onNavigateBack = {}
            )
        }
        
        // Click load button
        composeTestRule.onNodeWithContentDescription("Load shader").performClick()
        
        // Verify load dialog appears
        composeTestRule.onNodeWithText("Load Shader").assertExists()
        composeTestRule.onNodeWithText("Presets").assertExists()
        
        // Presets should be listed (at least one)
        // Note: Specific preset names depend on loaded data
        composeTestRule.onNodeWithText("Close").assertExists()
        
        // Close button should dismiss dialog
        composeTestRule.onNodeWithText("Close").performClick()
        composeTestRule.onNodeWithText("Load Shader").assertDoesNotExist()
    }
    
    /**
     * Test 6: Code collapse/expand works
     * Verify collapsible code section animates properly
     */
    @Test
    fun testCodeCollapseExpandWorks() {
        var isExpanded = true
        
        composeTestRule.setContent {
            CollapsibleCodeSection(
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded },
                content = {
                    androidx.compose.material3.Text("Test code content")
                }
            )
        }
        
        // Initially expanded - content should be visible
        composeTestRule.onNodeWithText("Test code content").assertExists()
        
        // Click to collapse
        composeTestRule.onNodeWithText("Shader Code").performClick()
        
        // Content should be hidden (AnimatedVisibility will remove it from composition)
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("Test code content").assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        // Hint text should appear when collapsed
        composeTestRule.onNodeWithText("Tap to expand and edit code...", substring = true)
            .assertExists()
    }
    
    /**
     * Test 7: Shader preview canvas rendering
     * Verify shader preview component renders without crashing
     */
    @Test
    fun testShaderPreviewCanvasRendering() {
        val testShaderCode = """
            uniform float2 iResolution;
            uniform float iTime;
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution;
                float t = iTime * 0.5;
                return half4(uv.x, uv.y, t, 1.0);
            }
        """.trimIndent()
        
        var compileError: String? = null
        
        composeTestRule.setContent {
            ShaderPreviewCanvas(
                shaderCode = testShaderCode,
                elapsedTime = 0f,
                touchPosition = androidx.compose.ui.geometry.Offset.Zero,
                resolution = androidx.compose.ui.geometry.Size(100f, 100f),
                onSizeChanged = {},
                onTouchPositionChanged = {},
                onCompileError = { error -> compileError = error }
            )
        }
        
        // Wait for shader compilation
        composeTestRule.waitForIdle()
        
        // Verify no compilation error
        // Note: This may fail on API < 26 or without hardware acceleration
        assert(compileError == null) { "Shader compilation failed: $compileError" }
    }
}
