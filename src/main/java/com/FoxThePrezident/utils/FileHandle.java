package com.FoxThePrezident.utils;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.Debug;

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
	private final String directory;
	/**
	 * List of all files for copying
	 */
	private final String[] files = new String[]{
			  "maps/mainMenu.json",
			  "maps/tutorial.json",
			  "menu.json",
			  "settings.json"
	};

	/**
	 * Constructor to initialize the directory path based on the operating system.
	 */
	public FileHandle() {
		if (Debug.utils.FileHandle) System.out.println(">>> [FileHandle.constructor]");

		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			directory = System.getenv("APPDATA") + "/One Button Game";
		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
			directory = System.getProperty("user.home") + "/.local/share/One Button Game";
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

			// Copy each file to the corresponding path in the target directory
			for (String file : files) {
				// Determine if the file has a subdirectory (e.g., "maps/mainMenu.json")
				Path targetFilePath = Paths.get(directoryPath.toString(), file);
				Path parentDir = targetFilePath.getParent();

				// Create the parent directory if it doesn't exist
				if (parentDir != null && !Files.exists(parentDir)) {
					Files.createDirectories(parentDir);
				}

				// Load and save the file
				String data = loadText("json/" + file, true);
				saveText("/" + file, data);
			}
		} catch (IOException e) {
			if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.initFiles]");
			throw new RuntimeException(e);
		}
		if (Debug.utils.FileHandle) System.out.println("<<< [FileHandle.initFiles]");
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
			throw new RuntimeException(e);
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
		String path = directory + File.separator + Directory;
		return Stream.of(Objects.requireNonNull(new File(path).listFiles()))
				  .filter(file -> !file.isDirectory())
				  .map(File::getName)
				  .collect(Collectors.toSet())
				  .toArray(String[]::new);

	}
}
