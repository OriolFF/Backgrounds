package com.uriolus.feature.shaders.editor2.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Handle shader file I/O operations
 */
class ShaderStorage(private val context: Context) {
    
    private val shadersDir: File
        get() = File(context.filesDir, "shaders").apply {
            if (!exists()) mkdirs()
        }
    
    /**
     * Save shader code to a file
     * @param name Name of the shader (without extension)
     * @param code Shader code content
     * @return Result with ShaderFile on success, error on failure
     */
    suspend fun saveShader(name: String, code: String): Result<ShaderFile> = withContext(Dispatchers.IO) {
        try {
            val sanitizedName = sanitizeFileName(name)
            val fileName = "$sanitizedName.agsl"
            val file = File(shadersDir, fileName)
            
            file.writeText(code)
            
            val shaderFile = ShaderFile(
                name = fileName,
                filePath = file.absolutePath,
                createdAt = if (file.exists()) file.lastModified() else System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis()
            )
            
            Result.success(shaderFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Load shader code from a file
     * @param file ShaderFile to load
     * @return Result with shader code on success, error on failure
     */
    suspend fun loadShader(file: ShaderFile): Result<String> = withContext(Dispatchers.IO) {
        try {
            val shaderFile = File(file.filePath)
            if (!shaderFile.exists()) {
                return@withContext Result.failure(Exception("Shader file not found: ${file.name}"))
            }
            
            val code = shaderFile.readText()
            Result.success(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * List all saved shader files
     * @return List of ShaderFile objects
     */
    suspend fun listSavedShaders(): List<ShaderFile> = withContext(Dispatchers.IO) {
        try {
            shadersDir.listFiles { file ->
                file.isFile && file.extension == "agsl"
            }?.map { file ->
                ShaderFile(
                    name = file.name,
                    filePath = file.absolutePath,
                    createdAt = file.lastModified(),
                    modifiedAt = file.lastModified()
                )
            }?.sortedByDescending { it.modifiedAt } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Delete a shader file
     * @param file ShaderFile to delete
     * @return Result with Unit on success, error on failure
     */
    suspend fun deleteShader(file: ShaderFile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val shaderFile = File(file.filePath)
            if (shaderFile.exists() && shaderFile.delete()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete file: ${file.name}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sanitize file name to remove invalid characters
     */
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9_-]"), "_")
    }
}
