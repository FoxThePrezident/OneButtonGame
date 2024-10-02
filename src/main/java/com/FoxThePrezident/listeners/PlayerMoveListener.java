package com.FoxThePrezident.listeners;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.entities.Player;
import com.FoxThePrezident.map.LevelEditor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Movement listener for player
 */
public class PlayerMoveListener implements KeyListener, MouseListener {
	private static boolean keyPressed = false;  // Track if a key is currently pressed

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (keyPressed) {
			return;  // Prevent additional movement while key is held down
		}

		keyPressed = true;  // Mark that a key is being held

		if (Data.LevelEditor.levelEdit) {
			LevelEditor.move(e.getKeyChar());
		} else {
			Player.action();  // Move the player
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPressed = false;  // Allow movement again when the key is released
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!Data.LevelEditor.levelEdit) {
			Player.action();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
