package com.FoxThePrezident.entities;

import com.FoxThePrezident.map.Collisions;
import com.FoxThePrezident.Data;
import com.FoxThePrezident.map.Icons;
import com.FoxThePrezident.map.Graphics;
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
		if (Data.debug) System.out.println(">>> [Player.run]");

		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		nextPosition = getNextPosition();
		if (Data.debug) System.out.println("--- [Player.run] Starting main loop for actions");
		while (Data.running) {
			// Checking if we could change an arrow direction
			if (System.currentTimeMillis() < lastMoveTime + Data.Player.controlDelay) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				continue;
			}

			// Updating the direction index
			directionIndex += 1;
			if (directionIndex > 3) directionIndex = 0;

			// Removing old arrow
			graphics.removeLayer(graphics.ARROW_LAYER);

			// Drawing new arrow
			nextPosition = getNextPosition();
			graphics.drawTile(nextPosition, getArrow(), graphics.ARROW_LAYER);

			// Resetting the last move time
			lastMoveTime = System.currentTimeMillis();
		}

		if (Data.debug) System.out.println("<<< [Player.run]");
	}

	/**
	 * Function, for dealing damage for player.
	 *
	 * @param damage which is dealt
	 */
	public static void getDamage(int damage) {
		if (Data.debug) System.out.println(">>> [Player.getDamage]");

		health -= damage;
		graphics.removeLayer(graphics.TEXT_LAYER);

		// Checking, if player is still alive
		if (health <= 0) {
			Data.running = false;
			graphics.removeLayer(graphics.ARROW_LAYER);
		} else {
			graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		}

		if (Data.debug) System.out.println("<<< [Player.getDamage]");
	}

	/**
	 * Function, for adding health to a player.
	 *
	 * @param heal which is added to a player
	 */
	public static void getHeal(int heal) {
		if (Data.debug) System.out.println("--- [Player.getHeal]");
		health += heal;
		graphics.removeLayer(graphics.TEXT_LAYER);
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
	}

	/**
	 * Method for handling movement of the player
	 */
	public static void move() {
		if (Data.debug) System.out.println(">>> [Player.move]");

		if (!Data.running) return;

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

		if (Data.debug) System.out.println("<<< [Player.move]");
	}

	/**
	 * Getting arrow based on a direction
	 *
	 * @return ImageIcon of correct arrow icon
	 */
	private ImageIcon getArrow() {
		if (Data.debug) System.out.println("--- [Player.getArrow]");

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
		if (Data.debug) System.out.println("--- [Player.getNextPosition]");

		int y = Data.Player.position[0] + DIRECTIONS[directionIndex][0];
		int x = Data.Player.position[1] + DIRECTIONS[directionIndex][1];
		return new int[]{y, x};
	}

	@Override
	public void onRefresh() {
		if (Data.debug) System.out.println(">>> [Player.onRefresh]");

		// Drawing player on the ENTITIES_LAYER
		graphics.drawTile(Data.Player.position, Icons.Player.player, graphics.ENTITIES_LAYER);

		// Drawing arrow on the ARROW_LAYER
		nextPosition = getNextPosition();
		graphics.drawTile(nextPosition, getArrow(), graphics.ARROW_LAYER);

		// Drawing HP text
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);

		if (Data.debug) System.out.println("<<< [Player.onRefresh]");
	}
}
