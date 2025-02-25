package com.OneOfManySimons.utils;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handling file related stuff, like loading and saving text and images.
 */
public class FileHandle {
	/**
	 * Application directory.<br>
	 * Pointing to the appropriate location based on the operating system.
	 */
	public final String directory;
	/**
	 * List of all files that needs to be copied
	 */
	private final String[] files = new String[]{
			"maps/first_level.json",
			"maps/introduction.json",
			"menu.json",
			"player_actions.json",
			"settings.json"
	};

	/**
	 * Constructor to initialize the directory path based on the operating system.
	 */
	public FileHandle() {
		if (Debug.utils.FileHandle) System.out.println(">>> [FileHandle.constructor]");

		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			directory = System.getenv("APPDATA") + "/OneButtonGame";
		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
			directory = System.getProperty("user.home") + "/.local/share/OneButtonGame";
		} else {
			if (Debug.utils.FileHandle) System.out.println("--- [FileHandle.constructor] Exception");
			throw new UnsupportedOperationException("Unsupported operating system: " + os);
		}
		if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.constructor]");
	}

	/**
	 * Initializing files and directories.
	 */
	public void initFiles() {
		if (Debug.utils.FileHandle) System.out.println(">>> [FileHandle.initFiles]");

		Path directoryPath = Paths.get(this.directory);

		try {
			// Create the main directory if it doesn't exist
			if (!Files.exists(directoryPath)) Files.createDirectories(directoryPath);

			for (String filePattern : files) {
				Path targetPath = Paths.get(directoryPath.toString(), filePattern);
				Path parentDir = targetPath.getParent();

				// If the entry contains '*', copy all files from that directory
				if (filePattern.contains("*")) {
					String dirPath = filePattern.substring(0, filePattern.indexOf("*"));
					copyAllFilesFromDirectory(dirPath);
					continue;
				}

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
			if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.initFiles] IOException");
			e.printStackTrace();
		} catch (Exception e) {
			if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.initFiles] Exception");
			System.out.println("Error handling files.");
		}

		if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.initFiles]");
	}

	/**
	 * Copies all files from a directory if a wildcard (*) is used in the file list.
	 */
	private void copyAllFilesFromDirectory(String directory) {
		if (Debug.utils.FileHandle) System.out.println(">>> [FileHandle.copyAllFilesFromDirectory]");

		Path sourceDir = Paths.get("json", directory);
		Path targetDir = Paths.get(this.directory, directory);

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
						System.err.println("Failed to copy: " + file.getFileName());
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			System.err.println("Error reading directory: " + directory);
			e.printStackTrace();
		}

		if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.copyAllFilesFromDirectory]");
	}

	/**
	 * Method for loading text content of a file.
	 *
	 * @param fileName of the file, we want the content of
	 * @param fromJar  whether we are loading from within a JAR file
	 * @return String content of that file
	 * @throws IOException when method cannot find a specified file
	 */
	public String loadText(String fileName, boolean fromJar) throws IOException {
		if (Debug.utils.FileHandle) System.out.println(">>> [FileHandle.loadText]");

		// Loading data from a jar file, used to run a game
		if (fromJar) {
			if (Debug.utils.FileHandle) System.out.println("--- [FileHandle.loadText] Loading from a JAR file");
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is == null) return null;
			InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			// Returning content of file
			if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.loadText]");
			return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
		}

		if (Debug.utils.FileHandle) System.out.println("--- [FileHandle.loadText] Loading from the game directory");
		Path filePath = Paths.get(directory, fileName);
		if (!Files.exists(filePath)) {
			throw new IOException("File not found: " + filePath);
		}

		String content = Files.readString(filePath, StandardCharsets.UTF_8);

		if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.loadText]");
		return content;
	}

	/**
	 * Writing text to a file.
	 *
	 * @param fileName or path of the file
	 * @param content  what we want to write
	 */
	public void saveText(String fileName, String content) {
		if (Debug.utils.FileHandle) System.out.println("--- [FileHandle.saveText]");

		try {
			File file = new File(directory + fileName);
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loading ImageIcon from specified file.
	 *
	 * @param path where the image is located
	 * @return ImageIcon
	 */
	public ImageIcon loadIcon(String path) {
		if (Debug.utils.FileHandle) System.out.println(">>> [FileHandle.loadIcon]");

		URL imageURL = getClass().getResource(path);
		if (imageURL == null) return null;
		ImageIcon rawIcon = new ImageIcon(imageURL);

		int width = rawIcon.getIconWidth();
		int height = rawIcon.getIconHeight();
		Image scaledImage = rawIcon.getImage().getScaledInstance(width * Data.imageScale, height * Data.imageScale, Image.SCALE_DEFAULT);

		if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.loadIcon]");
		return new ImageIcon(scaledImage);
	}

	public String[] getContentOfDirectory(String Directory) {
		if (Debug.utils.FileHandle) System.out.println("--- [FileHandle.getContentOfDirectory]");

		String path = directory + File.separator + Directory;
		return Stream.of(Objects.requireNonNull(new File(path).listFiles()))
				.filter(file -> !file.isDirectory())
				.map(File::getName)
				.collect(Collectors.toSet())
				.toArray(String[]::new);
	}
}
