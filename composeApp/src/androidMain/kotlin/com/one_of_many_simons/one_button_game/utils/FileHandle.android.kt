package com.one_of_many_simons.one_button_game.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Utils.FILE_UTILS
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.EXCEPTION
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.R
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
        debug(FILE_UTILS, CORE, ">>> [FileHandle.initFiles]")

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

        debug(FILE_UTILS, CORE, "<<< [FileHandle.initFiles]")
    }

    /**
     * Load text from the assets' folder.
     */
    private fun loadTextFromAssets(fileName: String): String? {
        debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadTextFromAssets]")

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
        debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadText]")

        val file = File(directory, fileName)
        if (!file.exists()) {
            debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.loadText] File not found: ${file.absolutePath}")
            return ""
        }

        return file.readText()
    }

    /**
     * Save text to a file.
     */
    actual fun saveText(fileName: String, content: String) {
        debug(FILE_UTILS, INFORMATION, "--- [FileHandle.saveText]")

        try {
            val file = File(directory, fileName)
            file.parentFile?.mkdirs()
            file.writeText(content)
        } catch (e: IOException) {
            debug(FILE_UTILS, INFORMATION, "--- [FileHandle.saveText] IOException: ${e.printStackTrace()}")
        }
    }

    /**
     * Load an image file as a byte array.
     */
    fun loadIcon(resId: Int): ImageBitmap {
        return try {
            val inputStream = context.resources.openRawResource(resId)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            val newWidth = (originalBitmap.width * Data.IMAGE_SCALE)
            val newHeight = (originalBitmap.height * Data.IMAGE_SCALE)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            scaledBitmap.asImageBitmap()
        } catch (e: Exception) {
            return loadIcon(R.drawable.blank)
        }
    }

    /**
     * Get the content of a directory.
     */
    actual fun getContentOfDirectory(directory: String): Array<String?> {
        debug(FILE_UTILS, INFORMATION, "--- [FileHandle.getContentOfDirectory]")

        val dir = File(this.directory, directory)
        return dir.listFiles()
            ?.filter { !it.isDirectory }
            ?.map { it.name }
            ?.toTypedArray()
            ?: emptyArray()
    }

    actual companion object {
        actual operator fun invoke(): FileHandle {
            return FileHandle()
        }
    }
}
