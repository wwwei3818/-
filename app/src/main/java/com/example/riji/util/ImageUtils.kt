package com.example.riji.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {

    /**
     * Copy image from content URI to app's internal storage
     * Returns the saved file path
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val imagesDir = File(context.filesDir, "diary_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val fileName = "diary_${UUID.randomUUID()}.jpg"
            val outputFile = File(imagesDir, fileName)

            // Use `use` on both streams to ensure proper cleanup
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Save multiple images and return their file paths
     */
    fun saveImagesToInternalStorage(context: Context, uris: List<Uri>): List<String> {
        val savedPaths = mutableListOf<String>()
        for (uri in uris) {
            val path = saveImageToInternalStorage(context, uri)
            if (path != null) {
                savedPaths.add(path)
            }
        }
        return savedPaths
    }

    /**
     * Delete image file from internal storage
     */
    fun deleteImage(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Delete multiple image files from comma-separated string
     */
    fun deleteImages(context: Context, filePaths: String) {
        filePaths.split(",").filter { it.isNotBlank() }.forEach { path ->
            deleteImage(context, path)
        }
    }
}
