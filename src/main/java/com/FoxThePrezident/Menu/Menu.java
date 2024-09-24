	package com.FoxThePrezident.Menu;

	import com.FoxThePrezident.Data;
	import com.FoxThePrezident.Debug;
	import com.FoxThePrezident.listeners.RefreshListener;
	import com.FoxThePrezident.map.Graphics;
	import com.FoxThePrezident.utils.FileHandle;
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
		private final FileHandle fileHandle = new FileHandle();
		private MenuCommands menuCommands;

		// Borders
		private static final Border borderPrimary = BorderFactory.createLineBorder(Color.RED, 3);
		private static final Border borderSecondary = BorderFactory.createLineBorder(Color.BLACK, 3);
		private static ArrayList<Border> borderList;

		private static boolean commandWithParameters = false;
		private static String commandName;

		public static boolean running = true;
		private static final String currentMenu = "MainMenu";
		private static ArrayList<JSONObject> menuItems;

		/**
		 * Initializing map
		 */
		public void init() {
			if (Debug.Menu.Menu) System.out.println(">>> [Menu.init]");

			menuCommands = new MenuCommands();
			menuItems = new ArrayList<>();

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
				if (Debug.Menu.Menu) System.out.println("<<< [Menu.init] Exception");
				throw new RuntimeException(e);
			}
			if (Debug.Menu.Menu) System.out.println("<<< [Menu.init]");
		}

		@Override
		public void run() {
			if (Debug.Menu.Menu) System.out.println(">>> [Menu.run]");

			init(); // Initialize menu items
			drawMenuItems();
			while (running) {
				drawMenuItems();

				try {
					// Delay for controlling the loop speed
					Thread.sleep(Data.Player.controlDelay * 2L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			if (Debug.Menu.Menu) System.out.println("<<< [Menu.run]");
		}

		/**
		 * Setting menu items, for example like new game
		 * @param newMenuItems ArrayList containing data about item
		 */
		public static void generateMenu(String CommandName, ArrayList<JSONObject> newMenuItems) {
			if (Debug.Menu.Menu) System.out.println(">>> [Menu.generateMenu]");

			commandWithParameters = true;
			commandName = CommandName;

			menuItems = newMenuItems;
			generateBorders();
			drawMenuItems();

			if (Debug.Menu.Menu) System.out.println("<<< [Menu.generateMenu]");
		}

		/**
		 * Method for drawing menu items on screen
		 */
		private static void drawMenuItems() {
			// Shift borders in the borderList (move the last to the first position)
			Border last = borderList.get(borderList.size() - 1);
			for (int i = borderList.size() - 2; i >= 0; i--) {
				borderList.set(i + 1, borderList.get(i));
			}
			borderList.set(0, last);

			int startY = 50; // Starting Y position for the menu
			int paddingY = 50; // Vertical space between each menu item

			// Clear previous layer
			graphics.removeLayer(graphics.TEXT_LAYER);

			// Draw all menu items with their current borders
			for (int i = 0; i < menuItems.size(); i++) {
				String menuItemText = menuItems.get(i).getString("label").replace("_", " "); // Format text
				graphics.drawText(new int[]{startY + i * paddingY, 64}, menuItemText, 20, Color.GRAY, borderList.get(i));
			}
		}

		/**
		 * Generating borders based on number of items
		 */
		private static void generateBorders() {
			if (Debug.Menu.Menu) System.out.println(">>> [Menu.generateBorders]");

			borderList = new ArrayList<>();

			// Set initial position for drawing the text
			for (int i = 0; i < menuItems.size() - 1; i++) {
				borderList.add(borderSecondary);
			}
			borderList.add(borderPrimary);

			if (Debug.Menu.Menu) System.out.println("<<< [Menu.generateBorders]");
		}

		/**
		 * Method for dynamically calling methods inside MenuCommands class
		 * @param action string of method, that needs to be called
		 */
		private void executeAction(String action) {
			if (Debug.Menu.Menu) System.out.println("--- [Menu.executeAction]");
			try {
				// Use reflection to call the method in MenuCommands
				Method method;
				if (!commandWithParameters) {
					method = menuCommands.getClass().getMethod(action);
					method.invoke(menuCommands);
				} else {
					method = menuCommands.getClass().getMethod(commandName, String.class);
					method.invoke(menuCommands, action);
				}
			} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void onRefresh() {
			if (Debug.Menu.Menu) System.out.println("--- [Menu.onRefresh]");
			// Determine which menu item is currently selected
			int selectedIndex = borderList.indexOf(borderPrimary);
			if (selectedIndex != -1) {
				JSONObject selectedItem = menuItems.get(selectedIndex);

				if (selectedItem.getString("itemType").equals("command")) {
					String action = selectedItem.optString("action");

					if (!commandWithParameters) {
						executeAction(action);
					} else {
						executeAction(selectedItem.getString("label"));
						commandWithParameters = false;
					}
				}
			}
		}

		@Override
		public int[] getPosition() {
			if (Debug.Menu.Menu) System.out.println("--- [Menu.getPosition]");
			return new int[0];
		}
	}
