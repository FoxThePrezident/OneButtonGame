package com.OneOfManySimons.menu;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.DataClasses.MenuItem;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.graphics.Text;
import com.OneOfManySimons.listeners.RefreshListener;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import static com.OneOfManySimons.Data.libraries.*;

/**
 * Main class for handling menu actions, like loading menu, changing or activating
 */
public class Menu implements Runnable, RefreshListener {
	// Borders
	private static final Border borderPrimary = BorderFactory.createLineBorder(Color.RED, 3);
	private static final Border borderSecondary = BorderFactory.createLineBorder(Color.BLACK, 3);
	private static MenuCommands menuCommands;
	private static ArrayList<Border> borderList;
	private static String currentMenu = "MainMenu";
	private static ArrayList<MenuItem> menuItems;
	public boolean running = true;

	/**
	 * Loading menu from JSON
	 */
	private static void loadMenu() {
		try {
			// Load JSON file containing the menu
			String menuRaw = fileHandle.loadText("menu.json", false);
			ArrayList<MenuItem> menu = gson.fromJson(menuRaw, new TypeToken<ArrayList<MenuItem>>(){}.getType());

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
			throw new RuntimeException(e);
		}
	}

	/**
	 * Setting menu items, for example like new game
	 *
	 * @param currMenu     currently active menu
	 * @param newMenuItems ArrayList containing data about item
	 */
	public static void generateMenu(String currMenu, ArrayList<MenuItem> newMenuItems) {
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
		Border last = borderList.get(borderList.size() - 1);
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
			String menuItemText = menuItems.get(i).label.replace("_", " "); // Format text
			Text text = new Text();
			text.setPosition(new Point(64, startY + i * paddingY));
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
			if (Debug.menu.Menu) System.out.println("--- [Menu.executeAction] Exception");
			System.err.println("Failed to execute action: " + action);
		}
	}


	@Override
	public void onRefresh() {
		if (Debug.menu.Menu) System.out.println("--- [Menu.onRefresh]");

		// Determine which menu item is currently selected
		int selectedIndex = borderList.indexOf(borderPrimary);
		if (selectedIndex != -1) {
			MenuItem selectedItem = menuItems.get(selectedIndex);

			try {
				switch (selectedItem.itemType) {
					case "command": {
						String action = selectedItem.action;
						String parameters = selectedItem.parameters;

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
						setMenu(selectedItem.label);
						break;
					}
				}
			} catch (NoSuchMethodException e) {
				if (Debug.menu.Menu) System.out.println("--- [Menu.onRefresh] NoSuchMethodException");
				e.printStackTrace();
			}
		}
	}

	@Override
	public Point getPosition() {
		if (Debug.menu.Menu) System.out.println("--- [Menu.getPosition]");
		return null;
	}
}
