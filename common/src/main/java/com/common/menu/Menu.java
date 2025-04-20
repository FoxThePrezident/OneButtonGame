package com.common.menu;

import com.common.Data;
import com.common.DataClasses.Colour;
import com.common.DataClasses.MenuItem;
import com.common.DataClasses.Position;
import com.common.DataClasses.TextData;
import com.common.listeners.Listeners;
import com.common.listeners.RefreshListener;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import static com.common.Data.*;
import static com.common.Debug.Flags.Menu.MENU;
import static com.common.Debug.Levels.*;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.TEXT_LAYER;

/**
 * Main class for handling menu actions, like loading menu, changing or activating
 */
public class Menu implements Runnable, RefreshListener {
	// Borders
	private static final Colour borderPrimary = new Colour(255, 0, 0);
	private static final Colour borderSecondary = new Colour(0, 0, 0);
	private static MenuCommands menuCommands;
	private static Colour[] borderList;
	private static String currentMenu = "MainMenu";
	private static ArrayList<MenuItem> menuItems;
	static boolean running = true;

	/**
	 * Loading menu from JSON
	 */
	private static void loadMenu() {
		debug(MENU, CORE, "--- [Menu.loadMenu]");

		try {
			// Load JSON file containing the menu
			String menuRaw = fileHandle.loadText("menu.json", false);
			ArrayList<MenuItem> menu = gson.fromJson(menuRaw, new TypeToken<ArrayList<MenuItem>>() {
			}.getType());

			assert menu != null;
			for (MenuItem menuItem : menu) {
				ArrayList<String> visible = menuItem.visible;
				for (String s : visible) {
					if (Objects.equals(s, currentMenu)) {
						menuItems.add(menuItem);
					}
				}
			}

			generateBorders();
		} catch (IOException e) {
			debug(MENU, CORE, "--- [Menu.loadMenu] IOException: " + e.getMessage());
		}
	}

	/**
	 * Setting menu items, for example like new game
	 *
	 * @param currMenu     currently active menu
	 * @param newMenuItems ArrayList containing data about item
	 */
	static void generateMenu(String currMenu, ArrayList<MenuItem> newMenuItems) {
		debug(MENU, CORE, ">>> [Menu.generateMenu]");

		currentMenu = currMenu;
		menuItems = newMenuItems;
		loadMenu();

		generateBorders();
		drawMenuItems();

		debug(MENU, CORE, "<<< [Menu.generateMenu]");
	}

	/**
	 * Method for drawing menu items on screen
	 */
	private static void drawMenuItems() {
		debug(MENU, CORE, ">>> [Menu.drawMenuItems]");

		// Shift borders in the borderList (move the last to the first position)
		Colour last = borderList[borderList.length - 1];
		for (int i = borderList.length - 2; i >= 0; i--) {
			borderList[i + 1] = borderList[i];
		}
		borderList[0] = last;

		int startY = 50; // Starting Y position for the menu
		int paddingY = 50; // Vertical space between each menu item

		// Clear previous layer
		graphics.clearLayer(TEXT_LAYER);

		// Draw all menu items with their current borders
		for (int i = 0; i < menuItems.size(); i++) {
			String menuItemText = menuItems.get(i).label.replace("_", " "); // Format text
			TextData text = new TextData();
			text.position = new Position(64, startY + i * paddingY);
			text.text = menuItemText;
			text.size = 20;
			text.backgroundColor = new Colour(100, 100, 100);
			text.borderColor = borderList[i];
			graphics.drawText(text);
		}

		debug(MENU, CORE, "<<< [Menu.drawMenuItems]");
	}

	/**
	 * Generating borders based on number of items
	 */
	private static void generateBorders() {
		debug(MENU, CORE, ">>> [Menu.generateBorders]");

		borderList = new Colour[menuItems.size()];

		// Set initial position for drawing the text
		for (int i = 0; i < menuItems.size() - 1; i++) {
			borderList[i] = borderSecondary;
		}
		borderList[menuItems.size() - 1] = borderPrimary;

		debug(MENU, CORE, "<<< [Menu.generateBorders]");
	}

	/**
	 * Initializing map
	 */
	public static void init() {
		debug(MENU, CORE, ">>> [Menu.init]");

		menuCommands = new MenuCommands();
		menuItems = new ArrayList<>();

		loadMenu();
		Listeners.addRefreshListener(new Menu());

		debug(MENU, CORE, "<<< [Menu.init]");
	}

	/**
	 * Open new menu
	 *
	 * @param newMenu that will be opened
	 */
	public static void setMenu(String newMenu) {
		debug(MENU, CORE, "--- [Menu.setMenu]");

		currentMenu = newMenu;
		running = true;

		init();

		Thread thread = new Thread(new Menu());
		thread.start();
	}

	@Override
	public void run() {
		debug(MENU, CORE, ">>> [Menu.run]");

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

		debug(MENU, CORE, "<<< [Menu.run]");
	}

	/**
	 * Method for dynamically calling methods inside MenuCommands class
	 *
	 * @param action string of method, that needs to be called
	 */
	private void executeAction(String action, String parameters) throws NoSuchMethodException {
		debug(MENU, CORE, "--- [Menu.executeAction]");

		try {
			Method method;
			if (Objects.equals(parameters, "")) {
				method = menuCommands.getClass().getMethod(action);
				method.invoke(menuCommands);
			} else {
				method = menuCommands.getClass().getMethod(action, String.class);
				method.invoke(menuCommands, parameters);
			}
		} catch (InvocationTargetException e) {
			debug(MENU, EXCEPTION, "--- [Menu.executeAction] InvocationTargetException: " + e.getMessage());
		} catch (IllegalAccessException e) {
			debug(MENU, EXCEPTION, "--- [Menu.executeAction] IllegalAccessException: " + e.getMessage());
		}
	}


	@Override
	public void onRefresh() {
		debug(MENU, CORE, "--- [Menu.onRefresh]");

		if (!running) {
			Listeners.removeRefreshListener(this);
			return;
		}

		// Determine which menu item is currently selected
		int selectedIndex = -1;
		for (int i = 0; i < borderList.length; i++) {
			if (borderList[i] == borderPrimary) {
				selectedIndex = i;
				break;
			}
		}
		if (selectedIndex != -1) {
			MenuItem selectedItem = menuItems.get(selectedIndex);

			try {
				switch (selectedItem.itemType) {
					case "command": {
						String action = selectedItem.action;
						String parameters = selectedItem.parameters;

						switch (action) {
							case "main_menu":
								menuCommands.main_menu();
								break;
							case "newGame":
								menuCommands.newGame();
								break;
							case "generateNewGame":
								menuCommands.generateNewGame(parameters);
								break;
							case "resumeGame":
								menuCommands.resumeGame();
								break;
							case "levelEditor":
								menuCommands.levelEditor();
								break;
							case "generateNewLevelEdit":
								MenuCommands.newMapLevelEdit(parameters);
								break;
							case "exitGame":
								menuCommands.exitGame();
								break;
							default:
								executeAction(action, parameters);
								break;
						}
						break;
					}
					case "menu": {
						setMenu(selectedItem.label);
						break;
					}
				}
			} catch (NoSuchMethodException e) {
				debug(MENU, EXCEPTION, "--- [Menu.onRefresh] NoSuchMethodException: " + e.getMessage());
			}
		}
	}

	@Override
	public void getEntityDamage(int damage) {
	}

	@Override
	public Position getPosition() {
		debug(MENU, INFORMATION, "--- [Menu.getPosition]");

		return null;
	}
}
