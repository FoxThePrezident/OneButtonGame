package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.map.LevelEditor.Companion.move
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * Movement listener for player
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlayerMoveListener : KeyListener, MouseListener {
    override fun keyTyped(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        if (keyPressed) {
            return  // Prevent additional movement while key is held down
        }

        keyPressed = true // Mark that a key is being held

        if (Data.LevelEditor.levelEdit) {
            move(e.keyChar)
        } else {
            Player.action() // Move the player
        }
    }

    override fun keyReleased(e: KeyEvent) {
        keyPressed = false // Allow movement again when the key is released
    }

    override fun mouseClicked(e: MouseEvent) {
        if (!Data.LevelEditor.levelEdit) {
            Player.action()
        }
    }

    override fun mousePressed(e: MouseEvent) {
    }

    override fun mouseReleased(e: MouseEvent) {
    }

    override fun mouseEntered(e: MouseEvent) {
    }

    override fun mouseExited(e: MouseEvent) {
    }

    companion object {
        private var keyPressed = false // Track if a key is currently pressed
    }
}
