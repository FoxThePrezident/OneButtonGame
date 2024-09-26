package com.FoxThePrezident.entities;

import com.FoxThePrezident.Debug;
import com.FoxThePrezident.map.Collisions;
import com.FoxThePrezident.Data;
import com.FoxThePrezident.map.Icons;
import com.FoxThePrezident.Menu.Graphics;
import com.FoxThePrezident.listeners.RefreshListener;

import javax.swing.*;

/**
 * Player class.<br>
 * Controlling movement and actions from and to player.
 */
public class Player implements Runnable, RefreshListener {
	public static int health = 15;

	private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	public static int directionIndex = 0;
	private static int[] nextPosition;

	private static long lastMoveTime = System.currentTimeMillis();

	private static final Collisions collisions = new Collisions();
	private static final Graphics graphics = new Graphics();

	/**
	 * Main thread for caning directions and arrows
	 */
	public void run() {
		if (Debug.entities.Player) System.out.println(">>> [Player.run]");

		if (!Data.running) {
			return;
		}
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		nextPosition = getNextPosition();

		if (Debug.entities.Player) System.out.println("--- [Player.run] Starting main loop for actions");
		while (Data.running) {
			// Calculate elapsed time since last move
			long elapsedTime = System.currentTimeMillis() - lastMoveTime;

			// Calculate time to wait if necessary
			long timeToWait = Data.Player.controlDelay - elapsedTime;
			if (timeToWait > 0) {
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				continue;
			}

			// Updating the direction index
			directionIndex += 1;
			if (directionIndex > 3) directionIndex = 0;

			// Removing old arrow
			graphics.clearLayer(graphics.ARROW_LAYER);

			// Drawing new arrow
			nextPosition = getNextPosition();
			graphics.drawTile(nextPosition, getArrow(), graphics.ARROW_LAYER);

			// Resetting the last move time
			lastMoveTime = System.currentTimeMillis();
		}

		if (Debug.entities.Player) System.out.println("<<< [Player.run]");
	}

	/**
	 * Function, for dealing damage for player.
	 *
	 * @param damage which is dealt
	 */
	public static void getDamage(int damage) {
		if (Debug.entities.Player) System.out.println(">>> [Player.getDamage]");

		if (damage <= 0) return;

		health -= damage;
		graphics.clearLayer(graphics.TEXT_LAYER);

		// Checking, if player is still alive
		if (health <= 0) {
			Data.running = false;
			graphics.clearLayer(graphics.ARROW_LAYER);
		} else {
			graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		}

		if (Debug.entities.Player) System.out.println("<<< [Player.getDamage]");
	}

	/**
	 * Function, for adding health to a player.
	 *
	 * @param heal which is added to a player
	 */
	public static void getHeal(int heal) {
		if (Debug.entities.Player) System.out.println("--- [Player.getHeal]");

		if (heal <= 0) return;
		health += heal;
		graphics.clearLayer(graphics.TEXT_LAYER);
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
	}

	/**
	 * Method for handling movement of the player
	 */
	public static void move() {
		if (Debug.entities.Player) System.out.println(">>> [Player.move]");

		if (!Data.running) {
			graphics.refreshScreen();
			return;
		}

		lastMoveTime = System.currentTimeMillis();

		// Getting next position
		nextPosition = getNextPosition();
		// Checking if player could move
		ImageIcon nextTile = graphics.getTile(nextPosition);
		int couldMove = collisions.checkForCollision(nextTile);
		if (couldMove == collisions.immovable) return;

		// Updating player position and refreshing screen
		Data.Player.position = nextPosition;
		graphics.refreshScreen();

		if (Debug.entities.Player) System.out.println("<<< [Player.move]");
	}

	/**
	 * Getting arrow based on a direction
	 *
	 * @return ImageIcon of correct arrow icon
	 */
	private ImageIcon getArrow() {
		if (Debug.entities.Player) System.out.println("--- [Player.getArrow]");

		return switch (directionIndex) {
			case 0 -> Icons.Player.up;
			case 1 -> Icons.Player.right;
			case 2 -> Icons.Player.down;
			case 3 -> Icons.Player.left;
			default -> Icons.Environment.blank;
		};
	}

	/**
	 * Getting the next position based on a direction
	 *
	 * @return int[] of next position
	 */
	private static int[] getNextPosition() {
		if (Debug.entities.Player) System.out.println("--- [Player.getNextPosition]");

		int y = Data.Player.position[0] + DIRECTIONS[directionIndex][0];
		int x = Data.Player.position[1] + DIRECTIONS[directionIndex][1];
		return new int[]{y, x};
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.Player) System.out.println("--- [Enemy.getPosition]");
		if (Data.LevelEditor.levelEdit) {
			return Data.LevelEditor.holdPosition;
		}
		return Data.Player.position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.Player) System.out.println(">>> [Player.onRefresh]");

		// Drawing player on the ENTITIES_LAYER
		if (Data.LevelEditor.levelEdit) {
			graphics.drawTile(Data.LevelEditor.holdPosition, Icons.Player.player, graphics.PLAYER_LAYER);
		} else {
			graphics.drawTile(Data.Player.position, Icons.Player.player, graphics.PLAYER_LAYER);
		}

		// Drawing arrow on the ARROW_LAYER
		if (Data.running) {
			nextPosition = getNextPosition();
			graphics.drawTile(nextPosition, getArrow(), graphics.ARROW_LAYER);

			// Drawing HP text
			graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		}

		if (Debug.entities.Player) System.out.println("<<< [Player.onRefresh]");
	}
}
