# Backgrounds 🎨

An Android app for creating and customizing beautiful, dynamic backgrounds and live wallpapers using algorithmic patterns and custom shaders.

## 📱 About

**Backgrounds** is a creative Android application that empowers users to design stunning visual backgrounds through two powerful approaches:

1. **Algorithmic Background Patterns** - Generate unique designs using advanced mathematical algorithms
2. **Custom Shader Programming** - Write your own AGSL shaders for complete creative control

Perfect for developers, designers, and anyone who loves beautiful, dynamic visuals on their device.

---

## ✨ Features

### 🌈 Background Generator

Create diverse and imaginative background patterns using advanced algorithms:

- **Aurora** - Mesmerizing northern lights effects with flowing colors
- **Voronoi** - Organic cellular patterns and Voronoi diagrams
- **Geometric** - Clean, mathematical geometric shapes and tessellations
- **Waves** - Fluid, wave-based animations and patterns
- **Perlin Noise** - Natural-looking organic textures and clouds

**Key Features:**
- 🎲 One-click randomize button for instant unique designs
- 🎨 Full customization controls for each algorithm
- 🖼️ Real-time preview with fullscreen mode
- 📤 Export generated backgrounds as theme code

### 🎮 Shader Editor

A full-featured AGSL (Android Graphics Shading Language) shader editor:

- ✍️ **Code-Friendly Text Input** - No autocorrect or autocapitalization interfering with your code
- 🔮 **Intelligent Autocomplete** - 60+ AGSL keywords, functions, data types, and uniforms
- 👁️ **Live Preview** - See your shader rendered in real-time as you type
- 📚 **Preset Library** - Collection of example shaders to learn from
- 📖 **Built-in Help** - Comprehensive AGSL documentation and syntax reference
- ⚡ **Uniform Controls** - Interactive controls for `iTime`, `iResolution`, and `iMouse`

**Autocomplete Includes:**
- Data types: `float`, `vec2`, `vec3`, `vec4`, `half4`, etc.
- Math functions: `sin`, `cos`, `smoothstep`, `mix`, `clamp`, etc.
- Vector operations: `normalize`, `dot`, `cross`, `length`, etc.
- Standard uniforms: `iTime`, `iResolution`, `iMouse`

---

## 🎓 What are Shaders?

**Shaders** are small programs that run on your device's GPU to calculate colors for each pixel on the screen. They enable incredibly efficient, smooth animations and effects that would be impossible with traditional CPU-based rendering.

### AGSL - Android Graphics Shading Language

AGSL is Android's shader language, introduced in Android 13 (API 33). It's based on GLSL (OpenGL ES Shading Language) but specifically designed to work seamlessly with Android's graphics system.

**Key Concepts:**
- **Per-Pixel Computation** - Your shader code runs once for every pixel
- **Uniforms** - Variables you can pass from your app to the shader (like time, resolution, mouse position)
- **GPU Accelerated** - Shaders run on the graphics processor for incredible performance
- **Declarative** - Write mathematical expressions to define what each pixel should look like

**Example Shader:**
```glsl
uniform shader iImage;

half4 main(vec2 fragCoord) {
    // Normalize coordinates (0.0 to 1.0)
    vec2 uv = fragCoord / iResolution.xy;
    
    // Create animated gradient
    float t = iTime * 0.5;
    vec3 color = vec3(
        sin(uv.x + t),
        cos(uv.y + t),
        0.5
    );
    
    return half4(color, 1.0);
}
```

---

## 🏗️ Architecture

This app follows modern Android development best practices:

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Multi-module MVVM with Clean Architecture
- **Dependency Injection**: Koin
- **Navigation**: Jetpack Navigation Compose
- **Graphics**: Android Canvas with RuntimeShader for AGSL

### Module Structure

```
backgrounds/
├── app/                    # Main application module
├── core/
│   ├── common/            # Shared utilities and base classes
│   ├── ui/               # Reusable UI components
│   └── navigation/       # Navigation configuration
└── feature/
    ├── backgrounds/      # Background pattern generator
    └── shaders/         # AGSL shader editor
```

**Requirements:**
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36

---

## 📚 Resources & Documentation

### Official Android Documentation

- [Android Graphics Shading Language (AGSL)](https://developer.android.com/develop/ui/views/graphics/agsl) - Official AGSL documentation
- [RuntimeShader Reference](https://developer.android.com/reference/android/graphics/RuntimeShader) - API reference for RuntimeShader
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern declarative UI toolkit

### AGSL Learning Resources

- [AGSL Overview on Medium](https://medium.com/androiddevelopers/android-graphics-shading-language-agsl-shader-effects-8c3c24ae45b0) - Introduction to AGSL and shader effects
- [Shadertoy](https://www.shadertoy.com/) - Community of shader creators (uses GLSL, similar to AGSL)
- [The Book of Shaders](https://thebookofshaders.com/) - Gentle introduction to fragment shaders

### Inspiration

This app draws inspiration from:
- **Shadertoy** - The premier platform for sharing and discovering shaders
- **Processing** - Creative coding framework emphasizing visual design
- **p5.js** - JavaScript library for creative coding

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK with API level 36

### Building the App

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Run the app on an emulator or physical device (Android 9.0+)

```bash
./gradlew :app:assembleDebug
```

### Running on Device

The app works best on devices running **Android 13 (API 33) or higher** for full shader support, but includes graceful fallbacks for older versions.

---

## 🎨 Usage Examples

### Creating a Background Pattern

1. Launch the app and navigate to **Background Generator**
2. Select an algorithm (Aurora, Voronoi, Geometric, Waves, or Perlin Noise)
3. Adjust parameters using the on-screen controls
4. Tap **Randomize** for instant variations
5. Export your favorite designs

### Writing a Custom Shader

1. Navigate to **Shader Editor**
2. Start typing shader code - autocomplete will suggest AGSL keywords
3. See your shader render in real-time in the preview area
4. Tap the Info button for AGSL syntax reference
5. Load presets from the library to learn techniques
6. Experiment with uniforms like `iTime` for animations

**Pro Tip**: Type just 2 letters and autocomplete will suggest matching AGSL functions!

---

## 🛠️ Development

### Key Technologies

- **Kotlin** - Modern, concise, and safe programming language
- **Jetpack Compose** - Declarative UI framework
- **Koin** - Lightweight dependency injection
- **Kotlin Coroutines** - Asynchronous programming
- **Material 3** - Modern design system

### Contributing

Contributions are welcome! Areas for improvement:
- Additional background algorithms
- More shader presets
- Syntax highlighting in the code editor
- Export shaders as wallpapers
- Line numbers in code editor

---

## 📄 License

This project is for educational and creative purposes.

---

## 🙏 Acknowledgments

- **[ShaderEditor](https://github.com/markusfisch/ShaderEditor)** by Markus Fisch - The shader editor in this app is based on this excellent project. We are grateful for making it available as open source.
- Android team for creating AGSL and RuntimeShader
- Shadertoy community for shader inspiration
- Open source community for amazing tools and libraries

---

## 📧 Contact

For questions, suggestions, or showcase your creations, feel free to reach out!

**Happy Creating! 🎨✨**
