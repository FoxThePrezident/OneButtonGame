package com.FoxThePrezident.entities;

import com.FoxThePrezident.common.Collisions;
import com.FoxThePrezident.common.Settings;
import com.FoxThePrezident.graphics.Icons;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.graphics.RefreshListener;

import javax.swing.*;

public class Player implements Runnable, RefreshListener {
	public static int[] position;
	public static int health = 15;

	private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	private static int directionIndex = 0;
	private static int[] nextPosition;

	private static long lastMoveTime = System.currentTimeMillis();

	private static final Collisions collisions = new Collisions();
	private static final Graphics graphics = new Graphics();

	public Player(int[] Position) {
		position = Position;
	}

	/**
	 * Main thread for caning directions and arrows
	 */
	public void run() {
		graphics.drawText(8, 8, health + " HP");
		nextPosition = getNextPosition();
		while (Settings.running) {
			// Checking if we could change an arrow direction
			if (System.currentTimeMillis() < lastMoveTime + Settings.playerControlDelay) {
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
			graphics.drawTile(nextPosition[0], nextPosition[1], getArrow(), graphics.ARROW_LAYER);

			// Resetting the last move time
			lastMoveTime = System.currentTimeMillis();
		}
	}

	public static void getDamage(int damage) {
		health -= damage;

		graphics.removeLayer(graphics.TEXT_LAYER);

		if (health <= 0) {
			Settings.running = false;
			graphics.removeLayer(graphics.ARROW_LAYER);
		} else {
			graphics.drawText(8, 8, health + " HP");
		}
	}

	public static void getHeal(int heal) {
		health += heal;
		graphics.removeLayer(graphics.TEXT_LAYER);
		graphics.drawText(8, 8, health + " HP");
	}

	/**
	 * Method for handling movement of the player
	 */
	public static void move() {
		if (!Settings.running) return;

		lastMoveTime = System.currentTimeMillis();

		// Getting next position
		nextPosition = getNextPosition();
		// Checking if player could move
		ImageIcon nextTile = graphics.getTile(nextPosition[0], nextPosition[1]);
		int couldMove = collisions.checkForCollision(nextTile);
		if (couldMove == collisions.immovable) return;

		// Updating player position and refreshing screen
		position = nextPosition;
		graphics.refreshScreen();
	}

	/**
	 * Getting arrow based on a direction
	 *
	 * @return ImageIcon of correct arrow icon
	 */
	private ImageIcon getArrow() {
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
		int y = position[0] + DIRECTIONS[directionIndex][0];
		int x = position[1] + DIRECTIONS[directionIndex][1];
		return new int[]{y, x};
	}

	@Override
	public void onRefresh() {
		// Drawing player on the ENTITIES_LAYER
		graphics.drawTile(position[0], position[1], Icons.Player.player, graphics.ENTITIES_LAYER);

		// Drawing arrow on the ARROW_LAYER
		nextPosition = getNextPosition();
		graphics.drawTile(nextPosition[0], nextPosition[1], getArrow(), graphics.ARROW_LAYER);

		// Drawing HP text
		graphics.drawText(8, 8, health + " HP");
	}
}
