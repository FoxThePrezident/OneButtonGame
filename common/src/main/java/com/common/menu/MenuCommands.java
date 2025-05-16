package com.common.menu;

import com.common.Data;
import com.common.DataClasses.MenuItem;
import com.common.DataClasses.Position;
import com.common.Launcher;
import com.common.entities.player.PlayerActions;
import com.common.graphics.Icons;
import com.common.graphics.TextInput;
import com.common.listeners.Listeners;
import com.common.listeners.NewMapListener;
import com.common.map.LevelEditor;

import java.util.ArrayList;

import static com.common.Data.*;
import static com.common.Debug.Flags.Menu.MENU_COMMANDS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.ACTIONS_LAYER;

/**
 * Contains all methods that are used in menus
 */
@SuppressWarnings("unused")
public class MenuCommands {
	private static final String new_map_name = "New map";

	void main_menu() {
		debug(MENU_COMMANDS, CORE, "--- [MenuCommands.main_menu]");

		ArrayList<MenuItem> menuItems = new ArrayList<>();
		Menu.generateMenu("MainMenu", menuItems);
	}

	/**
	 * Create new game with player
	 */
	void newGame() {
		debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.newGame]");

		ArrayList<String> maps = fileHandle.getContentOfDirectory("maps");
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

		debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.newGame]");
	}

	/**
	 * Generate new game for player
	 *
	 * @param mapName that will be loaded
	 */
	void generateNewGame(String mapName) {
		debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.generateNewGame]");

		// Clearing old things.
		Listeners.clearListeners();

		// Loading new ones.
		Data.Map.currentMap = mapName;
		Data.loadMap();
		Data.loadInteractive();

		Menu.running = false;
		Data.running = true;

		Launcher.createPlayer();

		debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.generateNewGame]");
	}

	/**
	 * Generate new menu with options for level editing
	 */
	void levelEditor() {
		debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.levelEditor]");

		// Getting maps that could be edited
		ArrayList<String> maps = fileHandle.getContentOfDirectory("maps");

		maps.set(maps.size() - 1, new_map_name);

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

		debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.levelEditor]");
	}

	/**
	 * Create new game in level edit mode
	 *
	 * @param mapName which map will be loaded
	 */
	public static void newMapLevelEdit(String mapName) {
		debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.newMapLevelEdit]");

		Menu.running = false;
		Launcher.levelEditor = new LevelEditor();

		if (mapName.equals(new_map_name)) {
			TextInput.open(new NewMapListener());
			return;
		} else {
			Data.Map.currentMap = mapName;
		}

		// Saving meanwhile position for player
		LevelEditor.init();
		Data.LevelEditor.holdPosition = new Position(Data.Player.position);
		Data.LevelEditor.levelEdit = true;
		graphics.resizeScreen();

		// Clearing old things.
		Listeners.clearListeners();

		// Loading new ones.
		Data.loadMap();
		Data.loadInteractive();
		Listeners.addRefreshListener(Launcher.levelEditor);

		Launcher.createPlayer();

		graphics.refreshScreen();
		graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, ACTIONS_LAYER);

		debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.newMapLevelEdit]");
	}

	/**
	 * Resuming game
	 */
	void resumeGame() {
		debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.resumeGame]");

		Menu.running = false;
		Data.running = true;
		graphics.refreshScreen();
		PlayerActions.init();
		Launcher.createPlayer();

		debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.resumeGame]");
	}

	/**
	 * Method for exiting game
	 */
	void exitGame() {
		debug(MENU_COMMANDS, CORE, "--- [MenuCommands.exitGame]");
		Menu.running = false;

		systemUtils.exit();
	}
}
