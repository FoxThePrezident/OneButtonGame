package com.one_of_many_simons.one_button_game.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Utils.FILE_UTILS
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.EXCEPTION
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import org.jetbrains.skia.Image
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import javax.imageio.ImageIO

/**
 * Handling file related stuff, like loading and saving text and images.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileHandle {
    /**
     * Application directory.<br></br>
     * Pointing to the appropriate location based on the operating system.
     */
    private var directory: String

    /**
     * List of all files that needs to be copied
     */
    private val files = arrayOf(
        "maps/first_level.json",
        "maps/introduction.json",
        "menu.json",
        "player_actions.json",
        "settings.json"
    )

    /**
     * Constructor to initialize the directory path based on the operating system.
     */
    init {
        debug(FILE_UTILS, CORE, ">>> [FileHandle.constructor]")

        val os = System.getProperty("os.name").lowercase(Locale.getDefault())

        directory = if (os.contains("win")) {
            System.getenv("APPDATA") + "/OneButtonGame"
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            System.getProperty("user.home") + "/.local/share/OneButtonGame"
        } else {
            debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.constructor] Exception")
            throw UnsupportedOperationException("Unsupported operating system: $os")
        }

        debug(FILE_UTILS, CORE, "<<< [FileHandle.constructor]")
    }

    /**
     * Initializing files and directories.
     */
    actual fun initFiles() {
        debug(FILE_UTILS, CORE, ">>> [FileHandle.initFiles]")

        val directoryPath = Paths.get(this.directory)

        try {
            // Create the main directory if it doesn't exist
            if (!Files.exists(directoryPath)) Files.createDirectories(directoryPath)

            for (filePattern in files) {
                val targetPath = Paths.get(directoryPath.toString(), filePattern)
                val parentDir = targetPath.parent

                // If the entry contains '*', copy all files from that directory
                if (filePattern.contains("*")) {
                    val dirPath = filePattern.substring(0, filePattern.indexOf("*"))
                    copyAllFilesFromDirectory(dirPath)
                    continue
                }

                // Create parent directories if necessary
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir)
                }

                // Check if the file already exists before copying
                if (!Files.exists(targetPath)) {
                    val data = loadText("json/$filePattern", true)
                    saveText("/$filePattern", data!!)
                }
            }
        } catch (e: IOException) {
            debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.initFiles] IOException: ${e.printStackTrace()}")
        } catch (e: Exception) {
            debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.initFiles] Exception: ${e.printStackTrace()}")
        }

        debug(FILE_UTILS, CORE, "<<< [FileHandle.initFiles]")
    }

    /**
     * Copies all files from a directory if a wildcard (*) is used in the file list.
     */
    private fun copyAllFilesFromDirectory(directory: String) {
        debug(FILE_UTILS, CORE, ">>> [FileHandle.copyAllFilesFromDirectory]")

        val sourceDir = Paths.get("json", directory)
        val targetDir = Paths.get(this.directory, directory)

        try {
            // Create the target directory if it doesn't exist
            if (!Files.exists(targetDir)) Files.createDirectories(targetDir)

            // Iterate over all files in the source directory
            Files.list(sourceDir).forEach { file: Path ->
                val targetFile = targetDir.resolve(file.fileName)
                if (!Files.exists(targetFile)) {
                    try {
                        val data = loadText(file.toString(), true)
                        saveText(targetFile.toString(), data!!)
                    } catch (e: IOException) {
                        debug(
                            FILE_UTILS,
                            EXCEPTION,
                            "<<< [FileHandle.copyAllFilesFromDirectory] IOException for failing to copy: ${file.fileName}, ${e.printStackTrace()}"
                        )
                    }
                }
            }
        } catch (e: IOException) {
            debug(
                FILE_UTILS,
                EXCEPTION,
                "<<< [FileHandle.copyAllFilesFromDirectory] IOException for directory: $directory, ${e.printStackTrace()}"
            )
        }

        debug(FILE_UTILS, CORE, "<<< [FileHandle.copyAllFilesFromDirectory]")
    }

    /**
     * Method for loading text content of a file.
     *
     * @param fileName of the file, we want the content of
     * @param fromJar  whether we are loading from within a JAR file
     * @return String content of that file
     * @throws IOException when method cannot find a specified file
     */
    @Throws(IOException::class)
    actual fun loadText(fileName: String, fromJar: Boolean): String? {
        debug(FILE_UTILS, CORE, ">>> [FileHandle.loadText]")

        // Loading data from a jar file, used to run a game
        if (fromJar) {
            debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadText] Loading from a JAR file")
            val classloader = Thread.currentThread().contextClassLoader
            val `is` = classloader.getResourceAsStream(fileName) ?: return null
            val reader = InputStreamReader(`is`, StandardCharsets.UTF_8)

            // Returning content of file
            debug(FILE_UTILS, CORE, "<<< [FileHandle.loadText]")
            return BufferedReader(reader).lines().collect(Collectors.joining("\n"))
        }

        debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadText] Loading from the game directory")
        val filePath = Paths.get(directory, fileName)

        if (!Files.exists(filePath)) {
            debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.loadText] File not found: $filePath")
        }

        val content = Files.readString(filePath, StandardCharsets.UTF_8)

        debug(FILE_UTILS, CORE, "<<< [FileHandle.loadText]")
        return content
    }

    /**
     * Writing text to a file.
     *
     * @param fileName or path of the file
     * @param content  what we want to write
     */
    actual fun saveText(fileName: String, content: String) {
        debug(FILE_UTILS, CORE, "--- [FileHandle.saveText]")

        try {
            val file = File(directory + File.separator + fileName)
            FileWriter(file).use { writer ->
                writer.write(content)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Loading ByteArray from specified file.
     *
     * @param path where the image is located
     * @return ByteArray
     */
    fun loadIcon(path: String): ImageBitmap {
        debug(FILE_UTILS, CORE, ">>> [FileHandle.loadIcon]")

        // Try to load from resources
        val resource: URL? = javaClass.getResource(path)
        if (resource == null) {
            val imageSize = Data.IMAGE_SIZE * Data.IMAGE_SCALE
            return ImageBitmap(imageSize, imageSize)
        }

        return try {
            val bytes = scaleImage(Files.readAllBytes(Path.of(resource.toURI())))
            debug(FILE_UTILS, CORE, "<<< [FileHandle.loadIcon]")
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        } catch (e: Exception) {
            debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.loadIcon] Exception with loading: $path")

            // Loading blank icon
            val imageSize = Data.IMAGE_SIZE * Data.IMAGE_SCALE
            ImageBitmap(imageSize, imageSize)
        }
    }

    /**
     * Function for upscaling loaded image
     */
    private fun scaleImage(imageData: ByteArray): ByteArray {
        debug(FILE_UTILS, CORE, ">>> [FileHandle.scaleImage]")

        // Convert byte array to BufferedImage
        val inputStream = ByteArrayInputStream(imageData)
        val originalImage = ImageIO.read(inputStream)

        // Calculate the new dimensions
        val newWidth = originalImage.width * Data.IMAGE_SCALE
        val newHeight = originalImage.height * Data.IMAGE_SCALE

        // Create a new image with the new dimensions
        val scaledImage = BufferedImage(newWidth, newHeight, originalImage.type)

        // Draw the original image scaled to the new size
        val graphics2D: Graphics2D = scaledImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
        graphics2D.dispose()

        // Convert the scaled image back to byte array
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(scaledImage, "png", outputStream)

        debug(FILE_UTILS, CORE, "<<< [FileHandle.scaleImage]")
        return outputStream.toByteArray()
    }

    /**
     * Returns array of files in a directory
     */
    actual fun getContentOfDirectory(directory: String): Array<String?> {
        debug(FILE_UTILS, INFORMATION, "--- [FileHandle.getContentOfDirectory]")

        val path = this.directory + File.separator + directory + File.separator
        return File(path).listFiles()
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
