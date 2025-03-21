package com.one_of_many_simons.one_button_game.entities.player

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Player.PLAYER
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.playerActions
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.dataClasses.TextData
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ACTIONS_LAYER
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.PLAYER_LAYER
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.TEXT_LAYER
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.DECOR_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener
import kotlin.math.sign


/**
 * PLAYER class.<br></br>
 * Controlling movement and actions from and to player.
 */
class Player : Runnable, RefreshListener {
    /**
     * LAUNCHER thread for caning directions and arrows
     */
    override fun run() {
        debug(PLAYER, CORE, ">>> [Player.run]")

        // Case when game is paused
        if (!Data.running) {
            return
        }

        // PLAYER health text
        drawHealth()

        //LAUNCHER changing actions loop
        debug(PLAYER, INFORMATION, "--- [Player.run] Starting main loop for actions")
        while (Data.running) {
            val elapsedTime = System.currentTimeMillis() - lastMoveTime
            val timeToWait = Data.Player.controlDelay - elapsedTime
            if (timeToWait > 0) {
                try {
                    Thread.sleep(timeToWait)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
                continue
            }

            playerActions.nextAction()
        }

        debug(PLAYER, CORE, "<<< [Player.run]")
    }

    override fun onRefresh() {
        debug(PLAYER, CORE, ">>> [Player.onRefresh]")

        if (Data.LevelEditor.levelEdit) {
            graphics.drawTile(Data.LevelEditor.holdPosition, Icons.Player.player, PLAYER_LAYER)
        } else {
            graphics.drawTile(Data.Player.position, Icons.Player.player, PLAYER_LAYER)
        }

        // Show outward arrows if the action icon is "menu"
        if (Data.running) {
            if (Data.Map.enemyCount <= 0) {
                Data.running = false

                val text = TextData()
                text.position = Position((Data.Player.radius - 1) * Data.IMAGE_SCALE * Data.IMAGE_SIZE, 0)
                text.text = "You won. For another try, please restart the game."
                text.isCentered = true

                graphics.clearLayer(ACTIONS_LAYER)
                graphics.drawTextField(text)
            } else {
                playerActions.drawAction()

                if (armor != null) {
                    graphics.drawTile(Data.Player.position, armor!!.icon, DECOR_LAYER)
                }

                drawHealth()
            }
        }

        debug(PLAYER, CORE, "<<< [Player.onRefresh]")
    }

    override fun getPosition(): Position {
        debug(PLAYER, INFORMATION, "--- [Player.getPosition]")

        if (Data.LevelEditor.levelEdit) {
            return Position(Data.LevelEditor.holdPosition)
        }
        return Position(Data.Player.position)
    }

    companion object {
        /**
         * Directions for placing player actions.<br></br>
         * Up, right, down, left, on player.
         */
        private val DIRECTIONS = arrayOf(Position(0, -1), Position(1, 0), Position(0, 1), Position(-1, 0), Position())

        /**
         * Players inventory
         */
        private val inventory = arrayOf(
            Item(Icons.LevelEditor.cursor, true),
            Item(Icons.LevelEditor.cursor, true),
            Item(Icons.LevelEditor.cursor, true),
            Item(Icons.LevelEditor.cursor, true)
        )

        /**
         * Player armor
         */
        private var armor: Armor? = null

        /**
         * Health of player.
         */
        var health: Int = 15

        /**
         * Time in milliseconds since player did last action
         */
        private var lastMoveTime = System.currentTimeMillis()

        fun addArmor(armor: Armor) {
            this.armor = armor
        }

        /**
         * Function, for dealing damage for player.
         *
         * @param damage which is dealt
         */
        fun getDamage(damage: Int) {
            debug(PLAYER, CORE, ">>> [Player.getDamage]")

            if (damage <= 0) {
                debug(PLAYER, CORE, "<<< [Player.getDamage]")
                return
            }

            // Lessening damage if armor is present
            if (armor != null) {
                val data = armor!!.getDamage(damage)
                val notBlockedDamage: Int = data.first
                val armorSurvivability: Boolean = data.second

                if (armorSurvivability) {
                    armor = null
                    graphics.clearLayer(ACTIONS_LAYER)
                }

                health -= notBlockedDamage
            } else {
                health -= damage
            }

            graphics.clearLayer(TEXT_LAYER)

            // Checking, if player is still alive
            if (health > 0) {
                drawHealth()
            } else { // PLAYER is no longer alive
                Data.running = false

                val text = TextData()
                text.text = "You died. Please restart the game for another try."
                text.position = Position((Data.Player.radius - 1) * Data.IMAGE_SCALE * Data.IMAGE_SIZE, 0)
                text.isCentered = true

                graphics.clearLayer(ACTIONS_LAYER)
                graphics.drawTextField(text)
            }

            debug(PLAYER, CORE, "<<< [Player.getDamage]")
        }

        /**
         * Function, for adding health to a player.
         *
         * @param heal which is added to a player
         */
        @JvmStatic
        fun getHeal(heal: Int) {
            debug(PLAYER, CORE, ">>> [Player.getHeal]")

            if (heal <= 0) return
            health += heal

            graphics.clearLayer(TEXT_LAYER)
            drawHealth()

            debug(PLAYER, CORE, "<<< [Player.getHeal]")
        }

        /**
         * Adding item to a player inventory
         *
         * @param item to add
         */
        fun addItem(item: Item) {
            debug(PLAYER, INFORMATION, "--- [Player.addItem]")

            for (i in inventory.indices) {
                if (inventory[i].isNull) {
                    inventory[i] = item
                    return
                }
            }
        }

        /**
         * Method for handling movement of the player
         */
        fun action() {
            debug(PLAYER, INFORMATION, "--- [Player.action]")

            playerActions.action()
        }

        /**
         * Getting the next position based on a direction
         *
         * @param direction custom direction of next position
         * @return Position of next position
         */
        fun getNextPosition(direction: Position?): Position {
            debug(PLAYER, INFORMATION, "--- [Player.getNextPosition]")

            var x = Data.Player.position.x
            var y = Data.Player.position.y

            if (direction == null) {
                x += DIRECTIONS[PlayerActions.actionIndex].x
                y += DIRECTIONS[PlayerActions.actionIndex].y
            } else {
                x += sign(direction.x.toDouble()).toInt()
                y += sign(direction.y.toDouble()).toInt()
            }
            return Position(x, y)
        }

        /**
         * Drawing current players health on screen
         */
        private fun drawHealth() {
            debug(PLAYER, INFORMATION, "--- [Player.drawHealth]")

            val text = TextData()
            text.text = "$health HP"
            text.position = Position(8, 8)

            graphics.drawTextField(text)
        }

        fun setInventoryItem(slot: Int, item: Item) {
            debug(PLAYER, INFORMATION, "--- [Player.setInventoryItem]")

            inventory[slot] = item
        }

        fun getInventoryItem(slot: Int): Item {
            debug(PLAYER, INFORMATION, "--- [Player.getInventoryItem]")

            return inventory[slot]
        }

        fun resetLatMoveTime() {
            debug(PLAYER, INFORMATION, "--- [Player.resetLatMoveTime]")
            lastMoveTime = System.currentTimeMillis()
        }
    }
}
