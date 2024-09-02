package com.FoxThePrezident.utils;

import com.FoxThePrezident.Data;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Handling file related stuff, like loading and saving text and images.
 */
public class FileHandle {
	/**
	 * Application directory.<br>
	 * Pointing to the appropriate location based on the operating system.
	 */
	private final String directory;

	/**
	 * Constructor to initialize the directory path based on the operating system.
	 */
	public FileHandle() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			directory = System.getenv("APPDATA") + "/One Button Game";
		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
			directory = System.getProperty("user.home") + "/.local/share/One Button Game";
		} else {
			throw new UnsupportedOperationException("Unsupported operating system: " + os);
		}
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
		if (Data.debug) System.out.println(">>> [FileHandle.loadText]");

		// Loading data from a jar file, used to run a game
		if (fromJar) {
			if (Data.debug) System.out.println("--- [FileHandle.loadText] Loading from a JAR file");
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is == null) return null;
			InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			// Returning content of file
			if (Data.debug) System.out.println("<<< [FileHandle.loadText]");
			return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
		}

		if (Data.debug) System.out.println("--- [FileHandle.loadText] Loading from the game directory");
		Path filePath = Paths.get(directory, fileName);
		if (!Files.exists(filePath)) {
			throw new IOException("File not found: " + filePath);
		}

		String content = Files.readString(filePath, StandardCharsets.UTF_8);

		if (Data.debug) System.out.println("<<< [FileHandle.loadText]");
		return content;
	}

	/**
	 * Writing text to a file.
	 *
	 * @param fileName or path of the file
	 * @param content  what we want to write
	 */
	public void saveText(String fileName, String content) {
		if (Data.debug) System.out.println("--- [FileHandle.saveText]");
		try {
			File file = new File(directory + fileName);
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(content);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initializing files and directories.
	 */
	public void initFiles() {
		if (Data.debug) System.out.println(">>> [FileHandle.initFiles]");

		Path directoryPath = Paths.get(this.directory);

		if (Files.exists(directoryPath)) return;

		try {
			Files.createDirectories(directoryPath);

			String data = loadText("json/settings.json", true);
			saveText("/settings.json", data);
		} catch (IOException e) {
			System.err.println("Failed to create directory: " + e.getMessage());
		}
		if (Data.debug) System.out.println("<<< [FileHandle.initFiles]");
	}

	/**
	 * Loading ImageIcon from specified file.
	 *
	 * @param path where the image is located
	 * @return ImageIcon
	 */
	public ImageIcon loadIcon(String path) {
		if (Data.debug) System.out.println(">>> [FileHandle.loadIcon]");

		URL imageURL = getClass().getResource(path);
		if (imageURL == null) return null;
		ImageIcon rawIcon = new ImageIcon(imageURL);

		int width = rawIcon.getIconWidth();
		int height = rawIcon.getIconHeight();
		Image scaledImage = rawIcon.getImage().getScaledInstance(width * Data.imageScale, height * Data.imageScale, Image.SCALE_DEFAULT);

		if (Data.debug) System.out.println("<<< [FileHandle.loadIcon]");
		return new ImageIcon(scaledImage);
	}
}
