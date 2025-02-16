package com.OneOfManySimons.entities.player;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.Item;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.graphics.Text;
import com.OneOfManySimons.listeners.RefreshListener;
import org.json.JSONArray;

/**
 * Player class.<br>
 * Controlling movement and actions from and to player.
 */
public class Player implements Runnable, RefreshListener {
	/**
	 * Directions for placing player actions.<br>
	 * Up, right, down, left, on player.
	 */
	private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};
	private static final Item[] inventory = new Item[]{
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true)
	};
	private static final Graphics graphics = new Graphics();
	private static final PlayerActions playerActions = new PlayerActions();
	/**
	 * Health of player.
	 */
	public static int health = 15;
	/**
	 * Time in milliseconds since player did last action
	 */
	private static long lastMoveTime = System.currentTimeMillis();

	/**
	 * Function, for dealing damage for player.
	 *
	 * @param damage which is dealt
	 */
	public static void getDamage(int damage) {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.getDamage]");

		if (damage <= 0) return;

		health -= damage;
		graphics.clearLayer(graphics.TEXT_LAYER);

		// Checking, if player is still alive
		if (health > 0) {
			DrawHealth();
		} else { // Player is no longer alive
			Data.running = false;

			Text text = new Text();
			text.setPosition(new int[]{(Data.Player.radius - 1) * Data.imageScale * Data.imageSize, 0});
			text.setText("You died. Please restart the game for another try.");
			text.setCentered(true);

			graphics.clearLayer(graphics.ARROW_LAYER);
			graphics.drawText(text);
		}

		if (Debug.entities.player.Player) System.out.println("<<< [Player.getDamage]");
	}

	/**
	 * Function, for adding health to a player.
	 *
	 * @param heal which is added to a player
	 */
	public static void getHeal(int heal) {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.getHeal]");

		if (heal <= 0) return;
		health += heal;

		Text text = new Text();
		text.setPosition(new int[]{8, 8});
		text.setText(health + " HP");
		text.setSize(25);

		graphics.clearLayer(graphics.TEXT_LAYER);
		graphics.drawText(text);

		if (Debug.entities.player.Player) System.out.println("<<< [Player.getHeal]");
	}

	/**
	 * Adding item to a player inventory
	 *
	 * @param item to add
	 */
	public static void addItem(Item item) {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.addItem]");
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i].getIsNull()) {
				inventory[i] = item;
				return;
			}
		}
		if (Debug.entities.player.Player) System.out.println("<<< [Player.addItem]");
	}

	/**
	 * Method for handling movement of the player
	 */
	public static void action() {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.action]");

		playerActions.action();

		if (Debug.entities.player.Player) System.out.println("<<< [Player.action]");
	}

	/**
	 * Getting the next position based on a direction
	 *
	 * @param direction custom direction of next position
	 * @return int[] of next position
	 */
	public static int[] getNextPosition(JSONArray direction) {
		if (Debug.entities.player.Player) System.out.println("--- [Player.getNextPosition]");

		int y = Data.Player.position[0];
		int x = Data.Player.position[1];

		if (direction == null) {
			y += DIRECTIONS[playerActions.getActionIndex()][0];
			x += DIRECTIONS[playerActions.getActionIndex()][1];
		} else {
			y += (int) Math.signum(direction.getInt(0));
			x += (int) Math.signum(direction.getInt(1));
		}
		return new int[]{y, x};
	}

	/**
	 * Drawing current players health on screen
	 */
	private static void DrawHealth() {
		Text text = new Text();
		text.setPosition(new int[]{8, 8});
		text.setText(health + " HP");
		text.setSize(25);
		graphics.drawText(text);
	}

	public static int[] getNextPosition() {
		return getNextPosition(null);
	}

	public static void setInventoryItem(int slot, Item item) {
		inventory[slot] = item;
	}

	public static Item getInventoryItem(int slot) {
		return inventory[slot];
	}

	public static void resetLatMoveTime() {
		lastMoveTime = System.currentTimeMillis();
	}

	/**
	 * Main thread for caning directions and arrows
	 */
	public void run() {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.run]");

		// Case when game is paused
		if (!Data.running) {
			return;
		}

		// Player health text
		DrawHealth();

		//Main changing actions loop
		if (Debug.entities.player.Player) System.out.println("--- [Player.run] Starting main loop for actions");
		while (Data.running) {
			long elapsedTime = System.currentTimeMillis() - lastMoveTime;
			long timeToWait = Data.Player.controlDelay - elapsedTime;
			if (timeToWait > 0) {
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				continue;
			}

			playerActions.nextAction();
		}

		if (Debug.entities.player.Player) System.out.println("<<< [Player.run]");
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.player.Player) System.out.println("--- [Player.getPosition]");
		if (Data.LevelEditor.levelEdit) {
			return Data.LevelEditor.holdPosition;
		}
		return Data.Player.position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.onRefresh]");

		if (Data.LevelEditor.levelEdit) {
			graphics.drawTile(Data.LevelEditor.holdPosition, Icons.Player.player, graphics.PLAYER_LAYER);
		} else {
			graphics.drawTile(Data.Player.position, Icons.Player.player, graphics.PLAYER_LAYER);
		}

		// Show outward arrows if the action icon is "menu"
		if (Data.running) {
			if (Data.Map.enemyCount <= 0) {
				Data.running = false;

				Text text = new Text();
				text.setPosition(new int[]{(Data.Player.radius - 1) * Data.imageScale * Data.imageSize, 0});
				text.setText("You won. For another try, please restart the game.");
				text.setCentered(true);

				graphics.clearLayer(graphics.ARROW_LAYER);
				graphics.drawText(text);
			} else {
				playerActions.drawAction();

				DrawHealth();
			}
		}

		if (Debug.entities.player.Player) System.out.println("<<< [Player.onRefresh]");
	}
}
