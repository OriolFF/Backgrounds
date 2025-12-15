package com.uriolus.feature.shaders.editor2.storage

/**
 * Data model for a saved shader file
 */
data class ShaderFile(
    val name: String,
    val filePath: String,
    val createdAt: Long,
    val modifiedAt: Long
) {
    /**
     * Get file extension
     */
    val extension: String
        get() = filePath.substringAfterLast('.', "")
    
    /**
     * Display name without extension
     */
    val displayName: String
        get() = name.substringBeforeLast('.', name)
}
