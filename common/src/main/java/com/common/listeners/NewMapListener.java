package com.common.listeners;

import com.common.Data;
import com.common.graphics.TextInput;
import com.common.menu.MenuCommands;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static com.common.Data.fileHandle;

public class NewMapListener implements ActionListener {
	/**
	 * Text input listener for text input for creating new game
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String map_name = TextInput.getText();
			String new_map = fileHandle.loadText("json/templates/map.json", true);
			fileHandle.saveText("/maps/" + map_name + ".json", new_map);
			Data.Map.currentMap = "map";
			TextInput.dispose();
			MenuCommands.newMapLevelEdit(map_name);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
