package com.FoxThePrezident.handlers;

import com.FoxThePrezident.common.Settings;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Handling file related stuff
 */
public class FileHandle {
	/**
	 * Method for loading text content of a file
	 *
	 * @param fileName of file, we want content od
	 * @return String content of that file
	 * @throws IOException when method cannot find a specified file
	 */
	public String loadText(String fileName) throws IOException {
		// Loading file
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(fileName);
		if (is == null) return null;

		// Returning content o file
		return new BufferedReader(
				  new InputStreamReader(is, StandardCharsets.UTF_8))
				  .lines()
				  .collect(Collectors.joining("\n"));
	}

	/**
	 * Loading ImageIcon from specified file
	 *
	 * @param path where is image located
	 * @return ImageIcon
	 */
	public ImageIcon loadIcon(String path) {
		// Loading image
		URL imageURL = getClass().getResource(path);
		if (imageURL == null) return null;
		ImageIcon rawIcon = new ImageIcon(imageURL);

		// Scaling image
		int width = rawIcon.getIconWidth();
		int height = rawIcon.getIconHeight();
		Image scaledImage = rawIcon.getImage().getScaledInstance(width * Settings.imageScale, height * Settings.imageScale, Image.SCALE_DEFAULT);
		return new ImageIcon(scaledImage);
	}

	/**
	 * Parsing string to JSON object
	 * @param data that we want to format
	 * @return JSON object
	 */
	public JSONObject parseToJSONObject(String data) {
		return new JSONObject(data);
	}
}
