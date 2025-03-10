package com.one_of_many_simons.one_button_game.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug
import org.jetbrains.skia.Image
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.*
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
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.constructor]")

        val os = System.getProperty("os.name").lowercase(Locale.getDefault())

        directory = if (os.contains("win")) {
            System.getenv("APPDATA") + "/OneButtonGame"
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            System.getProperty("user.home") + "/.local/share/OneButtonGame"
        } else {
            if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.constructor] Exception")
            throw UnsupportedOperationException("Unsupported operating system: $os")
        }
        if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.constructor]")
    }

    /**
     * Initializing files and directories.
     */
    actual fun initFiles() {
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.initFiles]")

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
            if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.initFiles] IOException")
            e.printStackTrace()
        } catch (e: Exception) {
            if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.initFiles] Exception")
            println("Error handling files.")
        }

        if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.initFiles]")
    }

    /**
     * Copies all files from a directory if a wildcard (*) is used in the file list.
     */
    private fun copyAllFilesFromDirectory(directory: String) {
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.copyAllFilesFromDirectory]")

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
                        System.err.println("Failed to copy: " + file.fileName)
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            System.err.println("Error reading directory: $directory")
            e.printStackTrace()
        }

        if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.copyAllFilesFromDirectory]")
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
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.loadText]")

        // Loading data from a jar file, used to run a game
        if (fromJar) {
            if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.loadText] Loading from a JAR file")
            val classloader = Thread.currentThread().contextClassLoader
            val `is` = classloader.getResourceAsStream(fileName) ?: return null
            val reader = InputStreamReader(`is`, StandardCharsets.UTF_8)

            // Returning content of file
            if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.loadText]")
            return BufferedReader(reader).lines().collect(Collectors.joining("\n"))
        }

        if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.loadText] Loading from the game directory")
        val filePath = Paths.get(directory, fileName)
        if (!Files.exists(filePath)) {
            throw IOException("File not found: $filePath")
        }

        val content = Files.readString(filePath, StandardCharsets.UTF_8)

        if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.loadText]")
        return content
    }

    /**
     * Writing text to a file.
     *
     * @param fileName or path of the file
     * @param content  what we want to write
     */
    actual fun saveText(fileName: String, content: String) {
        if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.saveText]")

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
    fun loadIcon(path: String): ImageBitmap? {
        if (Debug.Utils.FILE_UTILS) println(">>> [FileHandle.loadIcon]")

        // Try to load from resources
        val resource = javaClass.getResource(path) ?: return null

        return try {
            val bytes = scaleImage(Files.readAllBytes(Path.of(resource.toURI())))
            if (Debug.Utils.FILE_UTILS) println("<<< [FileHandle.loadIcon]")
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        } catch (e: Exception) {
            println("Error loading image: ${e.message}")
            null
        }
    }

    fun scaleImage(imageData: ByteArray): ByteArray {
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
        ImageIO.write(scaledImage, "png", outputStream)  // Change format if needed (e.g., "png")

        return outputStream.toByteArray()
    }

    actual fun getContentOfDirectory(directory: String): Array<String?> {
        if (Debug.Utils.FILE_UTILS) println("--- [FileHandle.getContentOfDirectory]")

        val path = this.directory + File.separator + directory + File.separator
        return File(path).listFiles()
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
