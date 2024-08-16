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
	 * Pointing to %appdata%
	 */
	private final String directory = System.getenv("APPDATA") + "/One Button Game";

	/**
	 * Method for loading text content of a file.
	 *
	 * @param fileName of file, we want content od
	 * @return String content of that file
	 * @throws IOException when method cannot find a specified file
	 */
	public String loadText(String fileName, boolean fromJar) throws IOException {
		if (Data.debug) System.out.println(">>> [FileHandle.loadText]");

		// Loading data from a jar file, used to run a game
		if (fromJar) {
			if (Data.debug) System.out.println("--- [FileHandle.loadText] Loading from a jar file");
			// Loading file
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is == null) return null;
			InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			// Returning content of file
			if (Data.debug) System.out.println("<<< [FileHandle.loadText]");
			return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
		}

		// Loading data from game directory
		if (Data.debug) System.out.println("--- [FileHandle.loadText] Loading from a game directory");
		StringBuilder content = new StringBuilder();

		try (FileReader reader = new FileReader(directory + "/" + fileName)) {
			int character;
			while ((character = reader.read()) != -1) {
				content.append((char) character);
			}
		}

		if (Data.debug) System.out.println("<<< [FileHandle.loadText]");
		return content.toString();
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

		// Create a Path object
		Path directory = Paths.get(this.directory);

		// Check if the directory exists
		if (Files.exists(directory)) return;

		try {
			Files.createDirectories(directory);

			String data = loadText("json/data.json", true);
			saveText("/data.json", data);
		} catch (IOException e) {
			System.err.println("Failed to create directory: " + e.getMessage());
		}
		if (Data.debug) System.out.println("<<< [FileHandle.initFiles]");
	}

	/**
	 * Loading ImageIcon from specified file.
	 *
	 * @param path where is image located
	 * @return ImageIcon
	 */
	public ImageIcon loadIcon(String path) {
		if (Data.debug) System.out.println(">>> [FileHandle.loadIcon]");

		// Loading image
		URL imageURL = getClass().getResource(path);
		if (imageURL == null) return null;
		ImageIcon rawIcon = new ImageIcon(imageURL);

		// Scaling image
		int width = rawIcon.getIconWidth();
		int height = rawIcon.getIconHeight();
		Image scaledImage = rawIcon.getImage().getScaledInstance(width * Data.imageScale, height * Data.imageScale, Image.SCALE_DEFAULT);

		if (Data.debug) System.out.println("<<< [FileHandle.loadIcon]");
		return new ImageIcon(scaledImage);
	}
}
