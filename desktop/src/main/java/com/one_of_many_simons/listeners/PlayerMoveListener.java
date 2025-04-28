package com.one_of_many_simons.listeners;

import com.common.Data;
import com.common.entities.player.Player;
import com.common.map.LevelEditor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Movement listener for player
 */
public class PlayerMoveListener implements KeyListener, MouseListener {
	private static boolean inputPressed = false;  // Track if a key is currently pressed

	private static final long longPressDelay = 250; // milliseconds
	private static long keyPressTime;

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Prevent additional movement while key is held down
		if (inputPressed) {
			return;
		}

		// Mark that a key is being held
		inputPressed = true;

		if (Data.LevelEditor.levelEdit) {
			LevelEditor.move(e.getKeyChar());
		} else {
			keyPressTime = System.currentTimeMillis();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Allow movement again when the key is released
		inputPressed = false;

		long duration = System.currentTimeMillis() - keyPressTime;

		if (duration > longPressDelay) {
			Player.longAction();
		} else {
			Player.shortAction();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Prevent additional movement while mouse is held down
		if (inputPressed) {
			return;
		}

		// Mark that a key is being held
		inputPressed = true;

		if (!Data.LevelEditor.levelEdit) {
			keyPressTime = System.currentTimeMillis();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Allow movement again when the mouse is released
		inputPressed = false;

		long duration = System.currentTimeMillis() - keyPressTime;

		if (duration > longPressDelay) {
			Player.longAction();
		} else {
			Player.shortAction();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
