package com.common.entities.player;

import com.common.Data;
import com.common.DataClasses.ArmorReturnDamage;
import com.common.DataClasses.Position;
import com.common.DataClasses.TextData;
import com.common.graphics.Icons;
import com.common.listeners.RefreshListener;

import java.util.Arrays;
import java.util.List;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Player.PLAYER;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.*;

/**
 * PLAYER class.<br></br>
 * Controlling movement and actions from and to player.
 */
public class Player implements Runnable, RefreshListener {
	/**
	 * Directions for placing player actions.<br></br>
	 * Up, right, down, left, on player.
	 */
	private static final List<Position> DIRECTIONS = Arrays.asList(new Position(0, -1), new Position(1, 0), new Position(0, 1), new Position(-1, 0));

	/**
	 * Players inventory
	 */
	@SuppressWarnings("FieldMayBeFinal")
	private static List<Item> inventory = Arrays.asList(
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true)
	);

	/**
	 * Player armor
	 */
	private static Armor armor;

	/**
	 * Health of player.
	 */
	private static int health = 15;

	/**
	 * Time in milliseconds since player did last shortAction
	 */
	private static long lastMoveTime = System.currentTimeMillis();

	/**
	 * LAUNCHER thread for caning directions and arrows
	 */
	@Override
	public void run() {
		debug(PLAYER, CORE, ">>> [Player.run]");

		// Case when game is paused
		if (!Data.running) {
			return;
		}

		// PLAYER health text
		drawHealth();

		//LAUNCHER changing actions loop
		debug(PLAYER, INFORMATION, "--- [Player.run] Starting main loop for actions");
		while (Data.running) {
			long elapsedTime = System.currentTimeMillis() - lastMoveTime;
			long timeToWait = Data.Player.controlDelay - elapsedTime;
			if (timeToWait > 0) {
				try {
					//noinspection BusyWait
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				continue;
			}

			PlayerActions.nextAction();
		}

		debug(PLAYER, CORE, "<<< [Player.run]");
	}

	@Override
	public void onRefresh() {
		debug(PLAYER, CORE, ">>> [Player.onRefresh]");

		if (Data.LevelEditor.levelEdit) {
			graphics.drawTile(Data.LevelEditor.holdPosition, Icons.Player.player, PLAYER_LAYER);
		} else {
			graphics.drawTile(Data.Player.position, Icons.Player.player, PLAYER_LAYER);
		}

		// Show outward arrows if the shortAction icon is "menu"
		if (Data.running) {
			if (Data.Map.enemyCount <= 0) {
				Data.running = false;

				TextData text = new TextData();
				text.position = new Position((Data.Player.radius - 1) * Data.IMAGE_SCALE * Data.IMAGE_SIZE, 0);
				text.text = "You won. For another try, please restart the game.";
				text.centered = true;

				graphics.clearLayer(ACTIONS_LAYER);
				graphics.drawText(text);
			} else {
				PlayerActions.drawAction();

				if (armor != null) {
					graphics.drawTile(Data.Player.position, armor.icon, DECOR_LAYER);
				}

				drawHealth();
			}
		}

		debug(PLAYER, CORE, "<<< [Player.onRefresh]");
	}

	@Override
	public Position getPosition() {
		debug(PLAYER, INFORMATION, "--- [Player.getPosition]");

		if (Data.LevelEditor.levelEdit) {
			return new Position(Data.LevelEditor.holdPosition);
		}
		return new Position(Data.Player.position);
	}

	static void addArmor(Armor armor) {
		Player.armor = armor;
	}

	public static int getHealth() {
		return health;
	}

	@Override
	public void getEntityDamage(int damage) {
		debug(PLAYER, CORE, "--- [Player.getEntityDamage]");
		if (damage > 0) {
			getDamage(damage);
		}
	}

	/**
	 * Function, for dealing damage for player.
	 *
	 * @param damage which is dealt
	 */
	public static void getDamage(int damage) {
		debug(PLAYER, CORE, ">>> [Player.getDamage]");

		if (damage <= 0) {
			debug(PLAYER, CORE, "<<< [Player.getDamage]");
			return;
		}

		// Lessening damage if armor is present
		if (armor != null) {
			ArmorReturnDamage data = armor.getDamage(damage);
			int notBlockedDamage = data.returnDamage;
			boolean armorSurvivability = data.destroyed;

			if (armorSurvivability) {
				armor = null;
				graphics.clearLayer(ACTIONS_LAYER);
			}

			health -= notBlockedDamage;
		} else {
			health -= damage;
		}

		graphics.clearLayer(TEXT_LAYER);

		// Checking, if player is still alive
		if (health > 0) {
			drawHealth();
		} else { // PLAYER is no longer alive
			Data.running = false;

			TextData text = new TextData();
			text.text = "You died. Please restart the game for another try.";
			text.position = new Position((Data.Player.radius - 1) * Data.IMAGE_SCALE * Data.IMAGE_SIZE, 0);
			text.centered = true;

			graphics.clearLayer(ACTIONS_LAYER);
			graphics.drawText(text);
		}

		debug(PLAYER, CORE, "<<< [Player.getDamage]");
	}

	/**
	 * Function, for adding health to a player.
	 *
	 * @param heal which is added to a player
	 */
	static void getHeal(int heal) {
		debug(PLAYER, CORE, ">>> [Player.getHeal]");

		if (heal <= 0) return;
		health += heal;

		graphics.clearLayer(TEXT_LAYER);
		drawHealth();

		debug(PLAYER, CORE, "<<< [Player.getHeal]");
	}

	/**
	 * Adding item to a player inventory
	 *
	 * @param item to add
	 */
	public static void addItem(Item item) {
		debug(PLAYER, INFORMATION, "--- [Player.addItem]");

		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).isNull) {
				inventory.set(i, item);
				return;
			}
		}
	}

	/**
	 * Method for handling short tap input for the player
	 */
	public static void shortAction() {
		debug(PLAYER, INFORMATION, "--- [Player.shortAction]");

		PlayerActions.shortAction();
	}

	/**
	 * Method for handling long input for the player
	 */
	public static void longAction() {
		debug(PLAYER, INFORMATION, "--- [Player.longAction]");

		PlayerActions.longAction();
	}

	/**
	 * Getting the next position based on a direction
	 *
	 * @param direction custom direction of next position
	 * @return Position of next position
	 */
	static Position getNextPosition(Position direction) {
		debug(PLAYER, INFORMATION, "--- [Player.getNextPosition]");

		int x = Data.Player.position.x;
		int y = Data.Player.position.y;

		if (direction == null) {
			x += DIRECTIONS.get(PlayerActions.actionIndex).x;
			y += DIRECTIONS.get(PlayerActions.actionIndex).y;
		} else {
			x += (int) Math.signum(direction.x);
			y += (int) Math.signum(direction.y);
		}
		return new Position(x, y);
	}

	/**
	 * Drawing current players health on screen
	 */
	private static void drawHealth() {
		debug(PLAYER, INFORMATION, "--- [Player.drawHealth]");

		TextData text = new TextData();
		text.text = health + " HP";
		text.position = new Position(8, 8);

		graphics.drawText(text);
	}

	static void setInventoryItem(int slot, Item item) {
		debug(PLAYER, INFORMATION, "--- [Player.setInventoryItem]");

		inventory.set(slot, item);
	}

	static Item getInventoryItem(int slot) {
		debug(PLAYER, INFORMATION, "--- [Player.getInventoryItem]");

		return inventory.get(slot);
	}

	static void resetLatMoveTime() {
		debug(PLAYER, INFORMATION, "--- [Player.resetLatMoveTime]");
		lastMoveTime = System.currentTimeMillis();
	}
}
