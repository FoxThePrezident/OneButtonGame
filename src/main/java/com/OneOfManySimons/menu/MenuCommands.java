package com.OneOfManySimons.menu;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.Main;
import com.OneOfManySimons.TextInput;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.map.LevelEditor;
import com.OneOfManySimons.utils.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contains all methods that are used in menus
 */
@SuppressWarnings("unused")
public class MenuCommands implements ActionListener {
	private static Menu menu;
	private static Listeners listeners;
	private final String new_map_name = "New map";

	public MenuCommands(Menu menu) {
		MenuCommands.menu = menu;
		listeners = new Listeners();
	}

	public void main_menu() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.main_menu]");

		ArrayList<JSONObject> menuItems = new ArrayList<>();
		Menu.generateMenu("MainMenu", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.main_menu]");
	}

	/**
	 * Create new game with player
	 */
	public void newGame() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.newGame]");

		FileHandle fileHandle = new FileHandle();
		String[] maps = fileHandle.getContentOfDirectory("maps");
		ArrayList<JSONObject> menuItems = new ArrayList<>();

		for (String map : maps) {
			JSONObject mapObject = new JSONObject();

			String mapName = map.replace(".json", "");

			mapObject.put("label", mapName);
			mapObject.put("itemType", "command");
			mapObject.put("action", "generateNewGame");
			mapObject.put("parameters", mapName);

			JSONArray visible = new JSONArray();
			visible.put("NewGame");
			mapObject.put("visible", visible);

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
		Listeners listeners = new Listeners();
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
		FileHandle file = new FileHandle();
		String[] maps = file.getContentOfDirectory("maps");

		maps = Arrays.copyOf(maps, maps.length + 1);
		maps[maps.length - 1] = new_map_name;

		ArrayList<JSONObject> menuItems = new ArrayList<>();

		for (String map : maps) {
			JSONObject mapObject = new JSONObject();

			String mapName = map.replace(".json", "");

			mapObject.put("label", mapName);
			mapObject.put("itemType", "command");
			mapObject.put("action", "generateNewLevelEdit");
			mapObject.put("parameters", mapName);

			JSONArray visible = new JSONArray();
			visible.put("NewGame");
			mapObject.put("visible", visible);

			menuItems.add(mapObject);
		}

		Menu.generateMenu("NewGame", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.LevelEditor]");
	}

	public void newMapLevelEdit(String mapName) throws IOException {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.newMapLevelEdit]");

		menu.running = false;
		LevelEditor editor = new LevelEditor();
		Graphics graphics = new Graphics();
		Listeners listeners = new Listeners();

		if (mapName.equals(new_map_name)) {
			new TextInput(this);
			return;
		} else {
			Data.Map.current = mapName;
		}

		// Saving meanwhile position for player
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];
		Data.LevelEditor.holdPosition = new int[]{y, x};
		Data.LevelEditor.levelEdit = true;
		graphics.resizeScreen();

		// Clearing old things.
		listeners.clearListeners();

		// Loading new ones.
		Data.loadMap();
		Data.loadInteractive();
		listeners.addRefreshListener(editor);

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
//		listeners.removeRefreshListener(Main.player);

		menu.running = false;

		Graphics graphics = new Graphics();
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
	 * TExt input listener for text input for creating new game
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			FileHandle file = new FileHandle();
			String map_name = TextInput.getText();
			String new_map = file.loadText("json/templates/map.json", true);
			file.saveText("/maps/" + map_name + ".json", new_map);
			Data.Map.current = "map";
			TextInput.disposeFrame();
			newMapLevelEdit(map_name);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
