package com.one_of_many_simons.one_button_game.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug
import java.io.File
import java.io.IOException

/**
 * Handling file-related operations on Android.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileHandle {
    private lateinit var context: Context

    /**
     * Application directory in Android's internal storage.
     */
    private lateinit var directory: File

    /**
     * List of all files that need to be copied.
     */
    private val files = arrayOf(
        "maps/first_level.json",
        "maps/introduction.json",
        "menu.json",
        "player_actions.json",
        "settings.json"
    )

    fun setContext(context: Context) {
        this.context = context
        directory = context.filesDir
    }

    /**
     * Initialize files and directories.
     */
    actual fun initFiles() {
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.initFiles]")

        try {
            for (filePattern in files) {
                val targetFile = File(directory, filePattern)

                // Create parent directories if necessary
                targetFile.parentFile?.mkdirs()

                // Copy files only if they don't exist
                if (!targetFile.exists()) {
                    val data = loadTextFromAssets(filePattern)
                    saveText(filePattern, data ?: "")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.initFiles]")
    }

    /**
     * Load text from the assets folder.
     */
    private fun loadTextFromAssets(fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load text content from a file.
     */
    actual fun loadText(fileName: String, fromJar: Boolean): String? {
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.loadText]")

        val file = File(directory, fileName)
        if (!file.exists()) {
            throw IOException("File not found: ${file.absolutePath}")
        }

        return file.readText()
    }

    /**
     * Save text to a file.
     */
    actual fun saveText(fileName: String, content: String) {
        if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.saveText]")

        try {
            val file = File(directory, fileName)
            file.parentFile?.mkdirs()
            file.writeText(content)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Load an image file as a byte array.
     */
    fun loadIcon(resId: Int): ImageBitmap? {
        return try {
            val inputStream = context.resources.openRawResource(resId)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            originalBitmap?.let { bitmap ->
                val newWidth = (bitmap.width * Data.IMAGE_SCALE)
                val newHeight = (bitmap.height * Data.IMAGE_SCALE)

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                scaledBitmap.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get the content of a directory.
     */
    actual fun getContentOfDirectory(directory: String): Array<String?> {
        if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.getContentOfDirectory]")

        val dir = File(this.directory, directory)
        return dir.listFiles()
            ?.filter { !it.isDirectory }
            ?.map { it.name }
            ?.toTypedArray()
            ?: emptyArray()
    }

    actual companion object {
        actual fun create(): FileHandle {
            return FileHandle()
        }
    }
}
