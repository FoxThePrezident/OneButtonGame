package com.one_of_many_simons.utils;

import com.common.Data;
import com.one_of_many_simons.DataClasses.ImageWrapper;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.common.Debug.Flags.Utils.FILE_UTILS;
import static com.common.Debug.Levels.*;
import static com.common.Debug.debug;

/**
 * Handling file related stuff, like loading and saving text and images.
 */
public class FileHandle extends com.common.utils.FileHandle {
	/**
	 * Application directory.<br>
	 * Pointing to the appropriate location based on the operating system.
	 */
	public static String directory;

	/**
	 * Initializing files and directories.
	 */
	public void initFiles() {
		debug(FILE_UTILS, CORE, ">>> [FileHandle.initFiles]");

		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			directory = System.getenv("APPDATA") + "/OneButtonGame";
		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
			directory = System.getProperty("user.home") + "/.local/share/OneButtonGame";
		} else {
			debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.initFiles] Unsupported operating system: " + os);
		}

		Path directoryPath = Paths.get(FileHandle.directory);

		try {
			// Create the main directory if it doesn't exist
			if (!Files.exists(directoryPath)) Files.createDirectories(directoryPath);

			for (String filePattern : files) {
				// If the entry contains '*', copy all files from that directory
				if (filePattern.contains("*")) {
					String dirPath = filePattern.substring(0, filePattern.indexOf("*"));
					copyAllFilesFromDirectory(dirPath);
					continue;
				}

				Path targetPath = Paths.get(directoryPath.toString(), filePattern);
				Path parentDir = targetPath.getParent();

				// Create parent directories if necessary
				if (parentDir != null && !Files.exists(parentDir)) {
					Files.createDirectories(parentDir);
				}

				// Check if the file already exists before copying
				if (!Files.exists(targetPath)) {
					String data = loadText("json/" + filePattern, true);
					saveText("/" + filePattern, data);
				}
			}
		} catch (IOException e) {
			debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.initFiles] IOException: " + e.getMessage());
		} catch (Exception e) {
			debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.initFiles] Exception: " + e.getMessage());
		}

		debug(FILE_UTILS, CORE, "<<< [FileHandle.initFiles]");
	}

	/**
	 * Copies all files from a directory if a wildcard (*) is used in the file list.
	 */
	private void copyAllFilesFromDirectory(String directory) {
		debug(FILE_UTILS, CORE, ">>> [FileHandle.copyAllFilesFromDirectory]");

		Path sourceDir = Paths.get("json", directory);
		Path targetDir = Paths.get(FileHandle.directory, directory);

		try {
			// Create the target directory if it doesn't exist
			if (!Files.exists(targetDir)) Files.createDirectories(targetDir);

			// Iterate over all files in the source directory
			//noinspection resource
			Files.list(sourceDir).forEach(file -> {
				Path targetFile = targetDir.resolve(file.getFileName());

				if (!Files.exists(targetFile)) {
					try {
						String data = loadText(file.toString(), true);
						saveText(targetFile.toString(), data);
					} catch (IOException e) {
						debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.copyAllFilesFromDirectory] Failed to copy: " + file.getFileName() + ", IOException: " + e.getMessage());
					}
				}
			});
		} catch (IOException e) {
			debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.copyAllFilesFromDirectory] Error reading directory: " + directory + ", IOException: " + e.getMessage());
		}

		debug(FILE_UTILS, CORE, "<<< [FileHandle.copyAllFilesFromDirectory]");
	}

	/**
	 * Method for loading text content of a file.
	 *
	 * @param fileName of the file, we want the content of
	 * @param fromJar  whether we are loading from within a JAR file
	 * @return String content of that file
	 */
	public String loadText(String fileName, boolean fromJar) throws IOException {
		debug(FILE_UTILS, CORE, ">>> [FileHandle.loadText]");

		// Loading data from a jar file, used to run a game
		if (fromJar) {
			debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadText] Loading from a JAR file");

			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is == null) return null;
			InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			// Returning content of file
			debug(FILE_UTILS, CORE, "<<< [FileHandle.loadText]");
			return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
		}

		debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadText] Loading from the game directory");
		Path filePath = Paths.get(directory, fileName);
		if (!Files.exists(filePath)) {
			throw new IOException("File not found: " + filePath);
		}

		StringBuilder content = new StringBuilder();
		List<String> lines = Files.readAllLines(filePath);
		for (String line: lines) {
			content.append(line);
		}

		debug(FILE_UTILS, CORE, "<<< [FileHandle.loadText]");
		return content.toString();
	}

	/**
	 * Writing text to a file.
	 *
	 * @param fileName or path of the file
	 * @param content  what we want to write
	 */
	public void saveText(String fileName, String content) {
		debug(FILE_UTILS, CORE, "--- [FileHandle.saveText]");

		try {
			File file = new File(directory + fileName);
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(content);
			}
		} catch (IOException e) {
			debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.saveText] IOException: " + e.getMessage());
		}
	}

	/**
	 * Loading ImageIcon from specified file.
	 *
	 * @param path where the image is located
	 * @return ImageIcon
	 */
	public ImageWrapper loadIcon(String path) {
		debug(FILE_UTILS, CORE, "--- [FileHandle.loadIcon]");

		URL imageURL = FileHandle.class.getResource(path);
		if (imageURL == null) return null;
		ImageIcon rawIcon = new ImageIcon(imageURL);

		int width = rawIcon.getIconWidth();
		int height = rawIcon.getIconHeight();
		Image scaledImage = rawIcon.getImage().getScaledInstance(width * Data.IMAGE_SCALE, height * Data.IMAGE_SCALE, Image.SCALE_DEFAULT);

		return new ImageWrapper(new ImageIcon(scaledImage));
	}

	public ArrayList<String> getContentOfDirectory(String directory) {
		debug(FILE_UTILS, INFORMATION, "--- [FileHandle.getContentOfDirectory]");

		String searchPath = FileHandle.directory + File.separator + directory;

		try (Stream<Path> stream = Files.list(Paths.get(searchPath))) {
			return stream
					.filter(file -> !Files.isDirectory(file))
					.map(Path::getFileName)
					.map(Path::toString)
					.distinct()
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (IOException e) {
			debug(FILE_UTILS, CORE, "--- [FileHandle.getContentOfDirectory] IOException: " + e.getMessage());
		}
		return new ArrayList<>();
	}
}
