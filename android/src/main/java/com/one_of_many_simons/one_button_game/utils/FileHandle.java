package com.one_of_many_simons.one_button_game.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.one_of_many_simons.one_button_game.DataClasses.ImageWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.common.Debug.Flags.Utils.FILE_UTILS;
import static com.common.Debug.Levels.*;
import static com.common.Debug.debug;

public class FileHandle extends com.common.utils.FileHandle {
	private final Context context;

	public FileHandle(Context context) {
		this.context = context;
	}

	/**
	 * Copies required files from assets if they don't exist in the app's storage.
	 */
	public void initFiles() {
		debug(FILE_UTILS, CORE, ">>> [FileHandle.initFiles]");

		File directory = context.getFilesDir();
		if (!directory.exists()) {
			directory.mkdirs();
		}

		for (String filePattern : files) {
			File targetFile = new File(directory, filePattern);

			// If the entry contains '*', copy all files from that directory
			if (filePattern.contains("*")) {
				String dirPath = filePattern.substring(0, filePattern.indexOf("*"));
				copyAllFilesFromDirectory(dirPath.replaceAll("/$", ""));
				continue;
			}

			if (!targetFile.exists()) {
				try {
					String data = loadText("json/" + filePattern, true);
					saveText(targetFile.getAbsolutePath(), data);
				} catch (IOException e) {
					debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.initFiles] IOException: " + e.getMessage());
				}
			}
		}

		debug(FILE_UTILS, CORE, "<<< [FileHandle.initFiles]");
	}

	/**
	 * Copies all files from an asset directory to internal storage if they don't already exist.
	 */
	public void copyAllFilesFromDirectory(String assetSubDir) {
		debug(FILE_UTILS, CORE, ">>> [FileHandle.copyAllFilesFromDirectory]");

		File targetDir = new File(context.getFilesDir(), assetSubDir);
		if (!targetDir.exists()) {
			boolean created = targetDir.mkdirs();
			if (!created) {
				debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.copyAllFilesFromDirectory] Failed to create target directory: " + targetDir.getAbsolutePath());
				return;
			}
		}

		try {
			String[] files = context.getAssets().list("json/" + assetSubDir); // Assuming "json" is your base folder in assets
			if (files == null) {
				debug(FILE_UTILS, CORE, "--- [FileHandle.copyAllFilesFromDirectory] No files found in assets/json/" + assetSubDir);
				return;
			}

			for (String fileName : files) {
				File targetFile = new File(targetDir, fileName);
				if (!targetFile.exists()) {
					String data = loadText("json/" + assetSubDir + "/" + fileName, true);
					saveText(targetFile.getAbsolutePath(), data);
				}
			}
		} catch (IOException e) {
			debug(FILE_UTILS, EXCEPTION, "<<< [FileHandle.copyAllFilesFromDirectory] IOException: " + e.getMessage());
		}

		debug(FILE_UTILS, CORE, "<<< [FileHandle.copyAllFilesFromDirectory]");
	}


	/**
	 * Saves text to internal storage.
	 */
	public void saveText(String filePath, String content) {
		debug(FILE_UTILS, CORE, "--- [FileHandle.saveText]");

		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write(content);
		} catch (IOException e) {
			debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.saveText] IOException: " + e.getMessage());
		}
	}

	@Override
	public ImageWrapper loadIcon(String path) {
		debug(FILE_UTILS, CORE, "--- [FileHandle.loadIcon]");

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		AssetManager assetManager = context.getAssets();
		try (InputStream is = assetManager.open(path)) {
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return new ImageWrapper(bitmap);
		} catch (IOException e) {
			debug(FILE_UTILS, EXCEPTION, "--- [FileHandle.loadIcon] IOException: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Load text content from a file.
	 */
	public String loadText(String fileName, boolean fromJar) throws IOException {
		debug(FILE_UTILS, INFORMATION, "--- [FileHandle.loadText] Loading: " + fileName);

		if (fromJar) {
			// Load from application assets
			AssetManager assetManager = context.getAssets();
			try (InputStream is = assetManager.open(fileName);
			     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				return reader.lines().collect(Collectors.joining("\n"));
			}
		}

		// Load from local storage
		File file = new File(context.getFilesDir(), fileName);
		if (!file.exists()) {
			throw new FileNotFoundException("File not found in local storage: " + file.getAbsolutePath());
		}

		try (InputStream is = new FileInputStream(file);
		     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

	/**
	 * Returns a list of files in a specified directory.
	 */
	public ArrayList<String> getContentOfDirectory(String directoryName) {
		debug(FILE_UTILS, INFORMATION, "--- [FileHandle.getContentOfDirectory]");

		ArrayList<String> fileNames = new ArrayList<>();
		File directory = new File(context.getFilesDir(), directoryName);
		File[] files = directory.listFiles();

		if (files != null) {
			HashSet<String> nameSet = new HashSet<>();
			for (File file : files) {
				if (file.isFile()) {
					String name = file.getName();
					if (nameSet.add(name)) {
						fileNames.add(name);
					}
				}
			}
		}
		return fileNames;
	}
}