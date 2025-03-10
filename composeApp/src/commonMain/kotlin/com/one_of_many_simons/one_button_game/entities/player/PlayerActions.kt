package com.one_of_many_simons.one_button_game.entities.player

import androidx.compose.ui.graphics.ImageBitmap
import com.google.gson.reflect.TypeToken
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.collisions
import com.one_of_many_simons.one_button_game.Data.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Data.Libraries.graphics
import com.one_of_many_simons.one_button_game.Data.Libraries.gson
import com.one_of_many_simons.one_button_game.Data.Libraries.menu
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.PlayerActionData
import com.one_of_many_simons.one_button_game.dataClasses.PlayerActionItem
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ARROW_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import java.io.IOException
import java.util.*

/**
 * Managing player actions, like inventory, movement ...
 */
class PlayerActions {
    fun init() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.init]")

        try {
            val actionsRaw: String? = fileHandle.loadText("player_actions.json", false)
            actionSets = gson.fromJson(
                actionsRaw,
                object : TypeToken<ArrayList<PlayerActionData?>?>() {
                }.type
            )
            currentActionSet = actionSets.first().items
        } catch (e: IOException) {
            if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.init] IOException")
            e.printStackTrace()
        }
    }

    /**
     * Changing current action
     */
    fun nextAction() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.nextAction]")

        actionIndex += 1
        if (actionIndex >= currentActionSet.size) actionIndex = 0
        currentAction = currentActionSet[actionIndex]
        graphics.clearLayer(ARROW_LAYER)
        drawAction()
        Player.resetLatMoveTime()
    }

    /**
     * Drawing current action
     */
    fun drawAction() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println(">>> [PlayerActions.drawAction]")

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
                graphics.drawTile(nextPosition!!, icon, ARROW_LAYER)
            }

            graphics.trigger()

            if (Debug.Entities.Player.PLAYER_ACTIONS) println("<<< [PlayerActions.drawAction]")
        } catch (exception: UninitializedPropertyAccessException) {
            return
        }
    }

    fun action() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println(">>> [PlayerActions.action]")

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

        if (Debug.Entities.Player.PLAYER_ACTIONS) println("<<< [PlayerActions.action]")
    }

    private fun move() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println(">>> [PlayerActions.move]")

        // Getting next position
        nextPosition = Player.getNextPosition(
            currentAction.vector
        )
        // Checking if player could move
        val nextTile: ImageBitmap = graphics.getTile(nextPosition!!) ?: return
        val couldMove: Int = collisions.checkForCollision(nextTile)
        if (couldMove == collisions.immovable) return

        // Updating player position and refreshing screen
        Data.Player.position = nextPosition as Position
        graphics.refreshScreen()

        if (Debug.Entities.Player.PLAYER_ACTIONS) println("<<< [PlayerActions.move]")
    }

    private fun inventory() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.inventory]")

        Player.getInventoryItem(actionIndex).applyEffects()
        Player.setInventoryItem(actionIndex, Item(Icons.LevelEditor.cursor!!, true))
    }

    private fun changeSet() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.changeSet]")

        actionIndex = -1
        for (actionObject in actionSets) {
            if (actionObject.name == currentAction.setName) {
                currentActionSet = actionObject.items
            }
        }
    }

    private fun menu() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.menu]")

        Data.running = false
        menu.setMenu(currentAction.menu)
    }

    /**
     * Drawing arrows pointing outwards from player
     */
    private fun drawOutwardArrows() {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println(">>> [PlayerActions.drawOutwardArrows]")

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
            graphics.drawTile(arrowPosition, arrowIcon, ARROW_LAYER)
        }

        if (Debug.Entities.Player.PLAYER_ACTIONS) println("<<< [PlayerActions.drawOutwardArrows]")
    }

    /**
     * Get icon for current action
     *
     * @param icon name
     * @return ByteArray of current action
     */
    private fun getIcon(icon: String): ImageBitmap? {
        if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.getIcon]")

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
            if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.getIcon] NoSuchFieldException")
//            throw RuntimeException(e)
            println(icon)
        } catch (e: IllegalAccessException) {
            if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- [PlayerActions.getIcon] IllegalAccessException")
            throw RuntimeException(e)
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
