package com.OneOfManySimons.menu;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.DataClasses.MenuItem;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.Main;
import com.OneOfManySimons.TextInput;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.map.LevelEditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.OneOfManySimons.Data.libaries.*;

/**
 * Contains all methods that are used in menus
 */
@SuppressWarnings("unused")
public class MenuCommands implements ActionListener {
	private static Menu menu;
	private final String new_map_name = "New map";

	public MenuCommands(Menu menu) {
		MenuCommands.menu = menu;
	}

	public void main_menu() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.main_menu]");

		ArrayList<MenuItem> menuItems = new ArrayList<>();
		Menu.generateMenu("MainMenu", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.main_menu]");
	}

	/**
	 * Create new game with player
	 */
	public void newGame() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.newGame]");

		String[] maps = fileHandle.getContentOfDirectory("maps");
		ArrayList<MenuItem> menuItems = new ArrayList<>();

		for (String map : maps) {
			MenuItem mapObject = new MenuItem();

			String mapName = map.replace(".json", "");

			mapObject.label = mapName;
			mapObject.itemType = "command";
			mapObject.action = "generateNewGame";
			mapObject.parameters = mapName;

			ArrayList<String> visible = new ArrayList<>();
			visible.add("NewGame");
			mapObject.visible = visible;

			menuItems.add(mapObject);
		}

		Menu.generateMenu("NewGame", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.newGame]");
	}

	/**
	 * Generate new game for player
	 *
	 * @param mapName that will be loaded
	 */
	public void generateNewGame(String mapName) {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.generateNewGame]");

		// Clearing old things.
		listeners.clearListeners();

		// Loading new ones.
		Data.Map.current = mapName;
		Data.loadMap();
		Data.loadInteractive();

		menu.running = false;
		Data.running = true;

		Main.createPlayer();

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.generateNewGame]");
	}

	public void levelEditor() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.LevelEditor]");

		// Getting maps that could be edited
		String[] maps = fileHandle.getContentOfDirectory("maps");

		maps = Arrays.copyOf(maps, maps.length + 1);
		maps[maps.length - 1] = new_map_name;

		ArrayList<MenuItem> menuItems = new ArrayList<>();

		for (String map : maps) {
			MenuItem mapObject = new MenuItem();

			String mapName = map.replace(".json", "");

			mapObject.label = mapName;
			mapObject.itemType = "command";
			mapObject.action = "generateNewLevelEdit";
			mapObject.parameters = mapName;

			ArrayList<String> visible = new ArrayList<>();
			visible.add("NewGame");
			mapObject.visible = visible;

			menuItems.add(mapObject);
		}

		Menu.generateMenu("NewGame", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.LevelEditor]");
	}

	public void newMapLevelEdit(String mapName) {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.newMapLevelEdit]");

		menu.running = false;
		LevelEditor levelEditor = new LevelEditor();

		if (mapName.equals(new_map_name)) {
			TextInput.open(this);
			return;
		} else {
			Data.Map.current = mapName;
		}

		// Saving meanwhile position for player
		Data.LevelEditor.holdPosition = new Point(Data.Player.position);
		Data.LevelEditor.levelEdit = true;
		graphics.resizeScreen();

		// Clearing old things.
		listeners.clearListeners();

		// Loading new ones.
		Data.loadMap();
		Data.loadInteractive();
		listeners.addRefreshListener(levelEditor);

		Main.createPlayer();

		graphics.refreshScreen();
		graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.newMapLevelEdit]");
	}

	/**
	 * Resuming game
	 */
	public void resumeGame() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.resumeGame]");

		listeners.removeRefreshListener(menu, true);

		menu.running = false;

		graphics.refreshScreen();

		Data.running = true;
		Thread player = new Thread(Main.player);
		player.start();

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.resumeGame]");
	}

	/**
	 * Method for exiting game
	 */
	public void exitGame() {
		if (Debug.menu.MenuCommands) System.out.println("--- [MenuCommands.exitGame]");
		menu.running = false;
		System.exit(0);
	}

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
			Data.Map.current = "map";
			TextInput.dispose();
			newMapLevelEdit(map_name);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
