package com.one_of_many_simons.one_button_game.entities.player

import androidx.compose.ui.graphics.ImageBitmap
import com.google.gson.reflect.TypeToken
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Player.PLAYER_ACTIONS
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.EXCEPTION
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.collisions
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.gson
import com.one_of_many_simons.one_button_game.Libraries.menu
import com.one_of_many_simons.one_button_game.dataClasses.PlayerActionData
import com.one_of_many_simons.one_button_game.dataClasses.PlayerActionItem
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ACTIONS_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import java.io.IOException
import java.util.*

/**
 * Managing player actions, like inventory, movement ...
 */
class PlayerActions {
    fun init() {
        debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.init]")

        try {
            val actionsRaw: String? = fileHandle.loadText("player_actions.json", false)
            actionSets = gson.fromJson(
                actionsRaw,
                object : TypeToken<ArrayList<PlayerActionData?>?>() {
                }.type
            )
            currentActionSet = actionSets.first().items

            currentAction = currentActionSet[1]

        } catch (e: IOException) {
            debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.init] IOException: ${e.printStackTrace()}")
        }
    }

    /**
     * Changing current action
     */
    fun nextAction() {
        debug(PLAYER_ACTIONS, INFORMATION, "--- [PlayerActions.nextAction]")

        actionIndex += 1
        if (actionIndex >= currentActionSet.size) actionIndex = 0
        currentAction = currentActionSet[actionIndex]
        graphics.clearLayer(ACTIONS_LAYER)
        drawAction()
        Player.resetLatMoveTime()
    }

    /**
     * Drawing current action
     */
    fun drawAction() {
        debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.drawAction]")

        graphics.clearLayer(ACTIONS_LAYER)

        try {
            val iconName = currentAction.icon

            if (iconName == "out") {
                drawOutwardArrows()
                graphics.trigger()
                return
            }

            nextPosition = Player.getNextPosition(null)
            val icon = getIcon(iconName)

            if (icon == null) {
                Player.getInventoryItem(actionIndex).draw(nextPosition!!)
            } else {
                graphics.drawTile(nextPosition!!, icon, ACTIONS_LAYER)
            }

            graphics.trigger()

            debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.drawAction]")
        } catch (e: UninitializedPropertyAccessException) {
            debug(
                PLAYER_ACTIONS,
                EXCEPTION,
                "<<< [PlayerActions.drawAction] UninitializedPropertyAccessException: ${e.printStackTrace()}"
            )
            return
        }
    }

    fun action() {
        debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.action]")

        if (!Data.running) {
            graphics.refreshScreen()
            return
        }

        Player.resetLatMoveTime()

        when (currentAction.action) {
            "move" -> move()
            "inventory" -> inventory()
            "changeSet" -> changeSet()
            "menu" -> menu()
        }

        debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.action]")
    }

    private fun move() {
        debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.move]")

        // Getting next position
        nextPosition = Player.getNextPosition(
            currentAction.vector
        )
        // Checking if player could move
        val nextTile: ImageBitmap = graphics.getTile(nextPosition!!)
        val couldMove: Int = collisions.checkForCollision(nextTile)
        if (couldMove == collisions.immovable) return

        // Updating player position and refreshing screen
        Data.Player.position = nextPosition as Position
        graphics.refreshScreen()

        debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.move]")
    }

    private fun inventory() {
        debug(PLAYER_ACTIONS, INFORMATION, "--- [PlayerActions.inventory]")

        Player.getInventoryItem(actionIndex).applyEffects()
        Player.setInventoryItem(actionIndex, Item(Icons.LevelEditor.cursor, true))
    }

    private fun changeSet() {
        debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.changeSet]")

        actionIndex = -1
        for (actionObject in actionSets) {
            if (actionObject.name == currentAction.setName) {
                currentActionSet = actionObject.items
            }
        }
    }

    private fun menu() {
        debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.menu]")

        Data.running = false
        menu.setMenu(currentAction.menu)
    }

    /**
     * Drawing arrows pointing outwards from player
     */
    private fun drawOutwardArrows() {
        debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.drawOutwardArrows]")

        val directions =
            arrayOf(Position(0, -1), Position(1, 0), Position(0, 1), Position(-1, 0)) // Up, Right, Down, Left
        val arrowIcons = arrayOf("Player.up", "Player.right", "Player.down", "Player.left") // Corresponding arrow icons

        // Draw arrows around the player in all directions
        for (i in directions.indices) {
            val direction = directions[i]
            val arrowPosition = Position(
                Data.Player.position.x + direction.x,
                Data.Player.position.y + direction.y
            )
            val arrowIcon = getIcon(arrowIcons[i])
            graphics.drawTile(arrowPosition, arrowIcon, ACTIONS_LAYER)
        }

        debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.drawOutwardArrows]")
    }

    /**
     * Get icon for current action
     *
     * @param icon name
     * @return ByteArray of current action
     */
    private fun getIcon(icon: String): ImageBitmap? {
        debug(PLAYER_ACTIONS, INFORMATION, "--- [PlayerActions.getIcon]")

        if (icon == "null") {
            return null
        }

        val iconPath = icon.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            val iconClass = Arrays.stream(Icons::class.java.declaredClasses)
                .filter { c: Class<*> -> c.simpleName == iconPath[0] }
                .findFirst()
                .orElse(null)

            // Get the field by name from the static Icons.PLAYER class
            val value: ImageBitmap?
            if (iconClass != null) {
                val field = iconClass.getField(iconPath[1])
                value = field[null] as ImageBitmap
            } else {
                value = Icons.Environment.blank
            }
            // Get the value of the field
            return value
        } catch (e: NoSuchFieldException) {
            debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.getIcon] NoSuchFieldException: ${e.printStackTrace()}")
        } catch (e: IllegalAccessException) {
            debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.getIcon] IllegalAccessException: ${e.printStackTrace()}")
        }
        return null
    }

    companion object {
        @JvmField
        var actionIndex: Int = 0
        private var nextPosition: Position? = null
        private lateinit var actionSets: ArrayList<PlayerActionData>
        private lateinit var currentActionSet: ArrayList<PlayerActionItem>
        private lateinit var currentAction: PlayerActionItem
    }
}
