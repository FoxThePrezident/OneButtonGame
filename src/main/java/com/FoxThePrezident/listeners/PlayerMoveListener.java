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
	@Override
	public void keyTyped(KeyEvent keyEvent) {
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		if (Data.LevelEditor.levelEdit) {
			LevelEditor.move(keyEvent.getKeyChar());
		} else {
			Player.move();
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!Data.LevelEditor.levelEdit) {
			Player.move();
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
