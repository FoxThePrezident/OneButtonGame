package com.OneOfManySimons.menu;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.graphics.Text;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.listeners.RefreshListener;
import com.OneOfManySimons.utils.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class Menu implements Runnable, RefreshListener {
	private static final Graphics graphics = new Graphics();
	private static final FileHandle fileHandle = new FileHandle();
	// Borders
	private static final Border borderPrimary = BorderFactory.createLineBorder(Color.RED, 3);
	private static final Border borderSecondary = BorderFactory.createLineBorder(Color.BLACK, 3);
	private static MenuCommands menuCommands;
	private static ArrayList<Border> borderList;
	private static String currentMenu = "MainMenu";
	private static ArrayList<JSONObject> menuItems;
	public boolean running = true;

	/**
	 * Loading menu from JSON
	 */
	private static void loadMenu() {
		try {
			// Load JSON file containing the menu
			String menuRaw = fileHandle.loadText("menu.json", false);
			JSONArray menu = new JSONArray(menuRaw);

			for (int i = 0; i < menu.length(); i++) {
				JSONObject menuItem = menu.getJSONObject(i);
				JSONArray visible = menuItem.getJSONArray("visible");
				for (int j = 0; j < visible.length(); j++) {
					if (Objects.equals(visible.getString(j), currentMenu)) {
						menuItems.add(menu.getJSONObject(i));
					}
				}
			}

			generateBorders();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Setting menu items, for example like new game
	 *
	 * @param currMenu     currently active menu
	 * @param newMenuItems ArrayList containing data about item
	 */
	public static void generateMenu(String currMenu, ArrayList<JSONObject> newMenuItems) {
		if (Debug.menu.Menu) System.out.println(">>> [Menu.generateMenu]");

		currentMenu = currMenu;
		menuItems = newMenuItems;
		loadMenu();

		generateBorders();
		drawMenuItems();

		if (Debug.menu.Menu) System.out.println("<<< [Menu.generateMenu]");
	}

	/**
	 * Method for drawing menu items on screen
	 */
	private static void drawMenuItems() {
		if (Debug.menu.Menu) System.out.println(">>> [Menu.drawMenuItems]");

		// Shift borders in the borderList (move the last to the first position)
		Border last = borderList.getLast();
		for (int i = borderList.size() - 2; i >= 0; i--) {
			borderList.set(i + 1, borderList.get(i));
		}
		borderList.set(0, last);

		int startY = 50; // Starting Y position for the menu
		int paddingY = 50; // Vertical space between each menu item

		// Clear previous layer
		graphics.clearLayer(graphics.TEXT_LAYER);

		// Draw all menu items with their current borders
		for (int i = 0; i < menuItems.size(); i++) {
			String menuItemText = menuItems.get(i).getString("label").replace("_", " "); // Format text
			Text text = new Text();
			text.setPosition(new int[]{startY + i * paddingY, 64});
			text.setText(menuItemText);
			text.setSize(20);
			text.setBackgroundColor(Color.GRAY);
			text.setBorder(borderList.get(i));
			graphics.drawText(text);
		}

		if (Debug.menu.Menu) System.out.println("<<< [Menu.drawMenuItems]");
	}

	/**
	 * Generating borders based on number of items
	 */
	private static void generateBorders() {
		if (Debug.menu.Menu) System.out.println(">>> [Menu.generateBorders]");

		borderList = new ArrayList<>();

		// Set initial position for drawing the text
		for (int i = 0; i < menuItems.size() - 1; i++) {
			borderList.add(borderSecondary);
		}
		borderList.add(borderPrimary);

		if (Debug.menu.Menu) System.out.println("<<< [Menu.generateBorders]");
	}

	/**
	 * Initializing map
	 */
	public void init() {
		if (Debug.menu.Menu) System.out.println(">>> [Menu.init]");

		menuCommands = new MenuCommands(this);
		menuItems = new ArrayList<>();
		Listeners listeners = new Listeners();

		loadMenu();
		listeners.addRefreshListener(this);

		if (Debug.menu.Menu) System.out.println("<<< [Menu.init]");
	}

	/**
	 * Open new menu
	 *
	 * @param newMenu that will be opened
	 */
	public void setMenu(String newMenu) {
		if (Debug.menu.Menu) System.out.println("--- [Menu.setMenu]");

//		Data.running = false;
		currentMenu = newMenu;
		running = true;

		init();

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		if (Debug.menu.Menu) System.out.println(">>> [Menu.run]");

		drawMenuItems();
		while (running) {
			drawMenuItems();

			try {
				// Delay for controlling the loop speed
				Thread.sleep(Data.Player.controlDelay * 2L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
		}

		if (Debug.menu.Menu) System.out.println("<<< [Menu.run]");
	}

	/**
	 * Method for dynamically calling methods inside MenuCommands class
	 *
	 * @param action string of method, that needs to be called
	 */
	private void executeAction(String action, String parameters) throws NoSuchMethodException {
		if (Debug.menu.Menu) System.out.println("--- [Menu.executeAction]");

		try {
			Method method;
			if (Objects.equals(parameters, "")) {
				method = menuCommands.getClass().getMethod(action);
				method.invoke(menuCommands);
			} else {
				method = menuCommands.getClass().getMethod(action, String.class);
				method.invoke(menuCommands, parameters);
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			System.err.println("Failed to execute action: " + action);
		}
	}


	@Override
	public void onRefresh() {
		if (Debug.menu.Menu) System.out.println("--- [Menu.onRefresh]");

		// Determine which menu item is currently selected
		int selectedIndex = borderList.indexOf(borderPrimary);
		if (selectedIndex != -1) {
			JSONObject selectedItem = menuItems.get(selectedIndex);

			try {
				switch (selectedItem.getString("itemType")) {
					case "command": {
						String action = selectedItem.optString("action");
						String parameters = selectedItem.optString("parameters");

						switch (action) {
							case "main_menu" -> menuCommands.main_menu();
							case "newGame" -> menuCommands.newGame();
							case "generateNewGame" -> menuCommands.generateNewGame(parameters);
							case "resumeGame" -> menuCommands.resumeGame();
							case "levelEditor" -> menuCommands.levelEditor();
							case "generateNewLevelEdit" -> menuCommands.newMapLevelEdit(parameters);
							case "exitGame" -> menuCommands.exitGame();
							default -> executeAction(action, parameters);
						}
						break;
					}
					case "menu": {
						setMenu(selectedItem.optString("menu"));
//					running = false;
						break;
					}
				}
			} catch (NoSuchMethodException | IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public int[] getPosition() {
		if (Debug.menu.Menu) System.out.println("--- [Menu.getPosition]");
		return new int[0];
	}
}
