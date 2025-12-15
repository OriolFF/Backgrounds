package com.uriolus.feature.shaders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShaderHelpScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AGSL Shader Reference") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IntroductionSection()
            UniformsSection()
            DataTypesSection()
            BuiltInFunctionsSection()
            ExamplesSection()
            TipsSection()
            AttributionSection()
        }
    }
}

@Composable
private fun IntroductionSection() {
    HelpSection(title = "Introduction") {
        Text(
            "AGSL (Android Graphics Shading Language) is a subset of GLSL ES that runs on Android's RuntimeShader. " +
                    "Shaders are programs that run on the GPU to generate graphics effects.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun UniformsSection() {
    HelpSection(title = "Available Uniforms") {
        Text(
            "Uniforms are variables passed from your app to the shader:",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        
        UniformItem(
            name = "iTime",
            type = "float",
            description = "Elapsed time in seconds since shader started. Updates at ~60 FPS for animations."
        )
        
        UniformItem(
            name = "iResolution",
            type = "float2",
            description = "Canvas size in pixels (width, height). Use for coordinate mapping."
        )
        
        UniformItem(
            name = "iMouse",
            type = "float2", 
            description = "Touch/mouse position in pixels (x, y). Updates when screen is touched/dragged. Use for interactive effects."
        )
        
        CodeBlock("""
// Example usage:
float2 uv = fragCoord / iResolution;
float2 mouseUV = iMouse / iResolution;

// Animate with time
float wave = sin(uv.x * 10.0 + iTime);

// React to touch/mouse position
float dist = length(uv - mouseUV);
        """.trimIndent())
    }
}

@Composable
private fun DataTypesSection() {
    HelpSection(title = "Data Types") {
        DataTypeItem("float", "Single floating-point number", "float x = 1.0;")
        DataTypeItem("float2", "Two floats (x, y)", "float2 pos = float2(0.5, 0.5);")
        DataTypeItem("float3", "Three floats (r, g, b) or (x, y, z)", "float3 color = float3(1.0, 0.5, 0.0);")
        DataTypeItem("float4", "Four floats (r, g, b, a)", "float4 rgba = float4(1.0, 0.0, 0.0, 1.0);")
        DataTypeItem("half4", "Return type for main() function", "half4 main(float2 fragCoord)")
        
        Spacer(Modifier.height(8.dp))
        Text(
            "💡 Tip: Use float for calculations, half4 only for the return value.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BuiltInFunctionsSection() {
    HelpSection(title = "Built-in Functions") {
        Text("Common math functions:", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        
        FunctionItem("sin(x), cos(x), tan(x)", "Trigonometric functions")
        FunctionItem("abs(x)", "Absolute value")
        FunctionItem("sqrt(x)", "Square root")
        FunctionItem("pow(x, y)", "x raised to power y")
        FunctionItem("mix(a, b, t)", "Linear interpolation: a * (1-t) + b * t")
        FunctionItem("clamp(x, min, max)", "Constrains x between min and max")
        FunctionItem("length(v)", "Length of vector v")
        FunctionItem("normalize(v)", "Returns unit vector in direction of v")
        FunctionItem("dot(a, b)", "Dot product of vectors")
        FunctionItem("step(edge, x)", "Returns 0 if x < edge, else 1")
        FunctionItem("smoothstep(e0, e1, x)", "Smooth interpolation between 0 and 1")
        
        Spacer(Modifier.height(12.dp))
        Text("Color functions:", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        
        FunctionItem("half4(r, g, b, a)", "Create color (values 0.0-1.0)")
        FunctionItem("float3(h, s, v)", "Can be used for HSV to RGB conversion")
    }
}

@Composable
private fun ExamplesSection() {
    HelpSection(title = "Example Patterns") {
        
        ExampleItem(
            title = "Animated Gradient",
            code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    
    float r = 0.5 + 0.5 * sin(iTime + uv.x * 3.0);
    float g = 0.5 + 0.5 * sin(iTime + uv.y * 3.0 + 2.0);
    float b = 0.5 + 0.5 * sin(iTime + (uv.x + uv.y) * 3.0 + 4.0);
    
    return half4(r, g, b, 1.0);
}
            """.trimIndent()
        )
        
        ExampleItem(
            title = "Interactive Circle (iMouse)",
            code = """
uniform float2 iResolution;
uniform float2 iMouse;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    float2 mouseUV = iMouse / iResolution;
    
    // Center coordinates
    uv = uv * 2.0 - 1.0;
    mouseUV = mouseUV * 2.0 - 1.0;
    uv.x *= iResolution.x / iResolution.y;
    mouseUV.x *= iResolution.x / iResolution.y;
    
    // Distance from mouse position
    float dist = length(uv - mouseUV);
    float circle = smoothstep(0.3, 0.28, dist);
    
    // Color based on distance
    float3 color = float3(circle * (1.0 - dist), 
                          circle * 0.5, 
                          circle * dist);
    
    return half4(color, 1.0);
}
            """.trimIndent()
        )
        
        ExampleItem(
            title = "Touch Ripple Effect",
            code = """
uniform float2 iResolution;
uniform float2 iMouse;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    float2 mouseUV = iMouse / iResolution;
    
    // Distance from touch point
    float dist = length(uv - mouseUV);
    
    // Ripple effect
    float ripple = sin(dist * 30.0 - iTime * 5.0);
    ripple *= exp(-dist * 3.0); // Fade with distance
    
    float3 color = float3(0.2, 0.5, 0.8) * (0.5 + 0.5 * ripple);
    
    return half4(color, 1.0);
}
            """.trimIndent()
        )
        
        ExampleItem(
            title = "Metaballs",
            code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    uv = uv * 2.0 - 1.0;
    uv.x *= iResolution.x / iResolution.y;
    
    // Create multiple moving metaballs
    float f = 0.0;
    f += 0.1 / length(uv - float2(sin(iTime) * 0.5, 
                                  cos(iTime * 0.7) * 0.5));
    f += 0.1 / length(uv - float2(cos(iTime * 1.3) * 0.5, 
                                  sin(iTime * 0.9) * 0.5));
    f += 0.1 / length(uv - float2(sin(iTime * 1.1) * 0.5, 
                                  cos(iTime * 1.5) * 0.5));
    
    float3 color = float3(f * 0.8, f * 0.5, f);
    
    return half4(color, 1.0);
}
            """.trimIndent()
        )
        
        ExampleItem(
            title = "Plasma Effect",
            code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution * 10.0;
    
    float v = sin(uv.x + iTime);
    v += sin(uv.y + iTime);
    v += sin((uv.x + uv.y) + iTime);
    v += sin(sqrt(uv.x * uv.x + uv.y * uv.y) + iTime);
    v *= 0.25;
    
    float3 color = float3(
        0.5 + 0.5 * sin(v * 3.14159),
        0.5 + 0.5 * sin(v * 3.14159 + 2.0),
        0.5 + 0.5 * sin(v * 3.14159 + 4.0)
    );
    
    return half4(color, 1.0);
}
            """.trimIndent()
        )
        
        ExampleItem(
            title = "Simple Ray Marching",
            code = """
uniform float2 iResolution;
uniform float iTime;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution * 2.0 - 1.0;
    uv.x *= iResolution.x / iResolution.y;
    
    // Camera ray
    float3 rayDir = normalize(float3(uv, 1.0));
    float3 rayPos = float3(0.0, 0.0, -3.0);
    
    // Sphere at origin
    float3 spherePos = float3(0.0, 0.0, 0.0);
    float t = 0.0;
    
    // Simple ray marching loop
    for(int i = 0; i < 32; i++) {
        float3 p = rayPos + rayDir * t;
        float dist = length(p - spherePos) - 1.0;
        if(dist < 0.01) break;
        t += dist;
    }
    
    float3 color = float3(1.0 - t * 0.15);
    
    return half4(color, 1.0);
}
            """.trimIndent()
        )
    }
}

@Composable
private fun TipsSection() {
    HelpSection(title = "Tips & Tricks") {
        TipItem("📐 Normalize Coordinates", "Divide fragCoord by iResolution to get 0-1 range")
        TipItem("🎯 Center Coordinates", "Use: uv = uv * 2.0 - 1.0 to get -1 to 1 range")
        TipItem("📺 Fix Aspect Ratio", "Multiply x by iResolution.x / iResolution.y")
        TipItem("🌈 Animate Colors", "Use sin(iTime + offset) for smooth color changes")
        TipItem("👆 Interactive Effects", "Use iMouse for touch-based interactions and cursor following")
        TipItem("💫 Smooth Falloff", "Use exp(-dist * factor) for natural distance-based fading")
        TipItem("⚡ Performance", "Avoid divisions in loops, precalculate constants")
        TipItem("🔍 Debug", "Output variables as colors: half4(value, value, value, 1.0)")
        TipItem("📏 Distance Fields", "Use length() and distance() for circles and shapes")
        TipItem("🎨 Mix Colors", "Use mix(color1, color2, factor) for blending")
        TipItem("🔄 Smooth Edges", "Use smoothstep(edge0, edge1, x) instead of step() for anti-aliasing")
        TipItem("🌊 Wave Patterns", "Combine multiple sin/cos with different frequencies for complex patterns")
    }
}

// Helper Composables

@Composable
private fun HelpSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun UniformItem(name: String, type: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DataTypeItem(type: String, description: String, example: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row {
            Text(
                text = type,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(80.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = example,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 80.dp)
        )
    }
}

@Composable
private fun FunctionItem(name: String, description: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
private fun ExampleItem(title: String, code: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        CodeBlock(code)
    }
}

@Composable
private fun CodeBlock(code: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFFD4D4D4),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun TipItem(title: String, description: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun AttributionSection() {
    HelpSection(title = "Acknowledgments") {
        Text(
            "This shader editor is based on the excellent ShaderEditor project by Markus Fisch:",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ShaderEditor",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "https://github.com/markusfisch/ShaderEditor",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            "Thank you to all contributors of the ShaderEditor project!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
