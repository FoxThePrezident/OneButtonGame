package com.common.utils;

import com.common.DataClasses.ImageWrapper;

import java.io.IOException;
import java.util.ArrayList;

public abstract class FileHandle {
	/**
	 * List of all files that needs to be copied
	 */
	public static final String[] files = new String[]{
			"maps/*",
			"menu.json",
			"player_actions.json",
			"settings.json"
	};

	public abstract void initFiles();

	public abstract String loadText(String fileName, boolean fromJar) throws IOException;

	public abstract void saveText(String fileName, String content);

	public abstract ImageWrapper loadIcon(String Path);

	public abstract ArrayList<String> getContentOfDirectory(String directory);
}
