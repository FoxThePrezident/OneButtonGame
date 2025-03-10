package com.one_of_many_simons.one_button_game.map

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.collisions
import com.one_of_many_simons.one_button_game.Data.Libraries.graphics
import com.one_of_many_simons.one_button_game.Data.Libraries.listeners
import com.one_of_many_simons.one_button_game.Data.Libraries.mapUtils
import com.one_of_many_simons.one_button_game.Data.saveMap
import com.one_of_many_simons.one_button_game.Data.saveSettings
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.Interactive
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.dataClasses.TextData
import com.one_of_many_simons.one_button_game.entities.enemies.Zombie
import com.one_of_many_simons.one_button_game.entities.potions.HP
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ARROW_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener
import com.one_of_many_simons.one_button_game.listeners.addSignMap
import com.one_of_many_simons.one_button_game.menu.MenuCommands

/**
 * Handling level editing.
 */
class LevelEditor : RefreshListener {
    /**
     * Top bar for easier keybindings
     */
    private val toolTable: LinkedHashMap<String?, ImageBitmap?> = object : LinkedHashMap<String?, ImageBitmap?>() {
        init {
            put("Void: 0", Icons.Environment.blank)
            put("Wall: 1", Icons.Environment.wall)
            put("Floor: 2", Icons.Environment.floor)
            put("PLAYER: 3", Icons.Player.player)
            put("ZOMBIE: 4", Icons.Enemies.zombie)
            put("HP: 5", Icons.Interactive.hp_potion)
            put("SIGN: 6", Icons.Interactive.sign)
        }
    }

    /**
     * Initializing viewport radius and other variables.
     */
    init {
        if (Debug.Map.LEVEL_EDITOR) println("--- [LevelEditor.constructor]")
        Data.Player.radius = 10
    }

    override fun onRefresh() {
        if (Debug.Map.LEVEL_EDITOR) println("--- [LevelEditor.onRefresh]")
        // Drawing edit cursor
        graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, ARROW_LAYER)

        val x = Data.Player.position.x - Data.Player.radius
        val y = Data.Player.position.y - Data.Player.radius
        val startPoint = Data.IMAGE_SIZE * Data.IMAGE_SCALE

        // Background
        val box = TextData()
        box.isCentered = true
        box.backgroundColor = Color.DarkGray
        box.borderColor = Color.Black
        box.text = "\n".repeat(Data.IMAGE_SCALE + 1)
        graphics.drawTextField(box)

        // Drawing tool tip menu items
        val entries: List<MutableMap.MutableEntry<String?, ImageBitmap?>> = toolTable.entries.toList()
        for (i in entries.indices) {
            val entry = entries[i]
            val text = TextData()
            text.backgroundColor = null
            text.borderColor = null
            text.textColor = Color.White

            // TEXT
            text.text = entry.key!!
            if (i % 2 == 0) {
                text.position = Position(startPoint * i, startPoint)
            } else {
                text.position = Position(startPoint * i, startPoint + 16)
            }
            graphics.drawTextField(text)

            // Icon
            graphics.drawTile(Position(x + i, y), entry.value, ARROW_LAYER)
        }

        graphics.trigger()
    }

    override fun getPosition(): Position? {
        return null
    }

    companion object {
        /**
         * Handling movement of a camera.
         *
         * @param keyChar of an input
         */
        @JvmStatic
        fun move(keyChar: Key) {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.move]")

            // Enter key, for not refreshing whole game to be able to see that game was saved
            if (keyChar == Key.Enter) {
                save()
                if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.move]")
                return
            }

            // Checking, which key was pressed
            when (keyChar) {
                Key.W -> Data.Player.position.y -= 1
                Key.D -> Data.Player.position.x += 1
                Key.S -> Data.Player.position.y += 1
                Key.A -> Data.Player.position.x -= 1
                Key.Zero -> changeTile("")
                Key.NumPad0 -> changeTile("")
                Key.One -> changeTile("W")
                Key.NumPad1 -> changeTile("W")
                Key.Two -> changeTile(" ")
                Key.NumPad2 -> changeTile(" ")
                Key.Three -> movePlayer()
                Key.NumPad3 -> movePlayer()
                Key.Four -> addEntity("zombie")
                Key.NumPad4 -> addEntity("zombie")
                Key.Five -> addEntity("hp")
                Key.NumPad5 -> addEntity("hp")
                Key.Six -> addSign()
                Key.NumPad6 -> addSign()
                Key.Q -> showMenu()
                else -> println(keyChar)
            }
            // Refreshing screen after each input
            graphics.refreshScreen()

            if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.move]")
        }

        private fun showMenu() {
            if (Debug.Entities.Player.PLAYER_ACTIONS) println("--- LevelEditor.menu")

            Data.running = false
            MenuCommands.menu!!.setMenu("InGameMenu")
        }

        /**
         * Placing player on a cursor position.
         */
        private fun movePlayer() {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.movePlayer]")

            // Checking, if we need to shift map
            checkForShift()
            if (removeEntity(false)) return

            // Changing hold position
            Data.LevelEditor.holdPosition = Position(Data.Player.position)

            if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.movePlayer]")
        }

        /**
         * Adding entity or interactive thing to a map.
         *
         * @param entityName which we want to place at cursor position
         */
        private fun addEntity(entityName: String) {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.addEntity]")

            checkForShift()
            if (removeEntity(false)) return

            // Getting coordinates
            val position = Position(Data.Player.position)

            // Checking, which type of entity we want to add
            val entity = Interactive()
            entity.position = position
            when (entityName) {
                "zombie" -> {
                    entity.entityType = "zombie"
                    val zombie = Zombie(position)
                    listeners.addRefreshListener(zombie)
                }

                "hp" -> {
                    entity.entityType = "hp"
                    val hp = HP(position)
                    listeners.addRefreshListener(hp)
                }

                else -> {
                    if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.addEntity]")
                    return
                }
            }
            Data.Map.interactive.add(entity)

            if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.addEntity]")
        }

        /**
         * Adding sign to a map
         */
        private fun addSign() {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.addSign]")

            checkForShift()
            if (removeEntity(false)) return

            Data.Libraries.textInputListeners.show { addSignMap() }

            if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.addSign]")
        }

        /**
         * Changing tile in a map.
         *
         * @param tile which we want to place/override in a map
         */
        private fun changeTile(tile: String) {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.changeTile]")

            if (removeEntity(true)) return

            // Checking if we need to shift the map and getting the row to change
            val rowArray = checkForShift()
            val position = Position(Data.Player.position)

            // Ensure the row has enough length
            while (rowArray.size <= position.x) {
                rowArray.add("")
            }

            // Replace the tile at the current cursor position
            rowArray[position.x] = tile

            // Update the row in the map
            if (position.y < Data.map!!.size) {
                Data.map!![position.y] = rowArray
            } else {
                // Add new rows if necessary
                while (Data.map!!.size <= position.y) {
                    Data.map!!.add(ArrayList())
                }
                Data.map!![position.y] = rowArray
            }

            graphics.refreshScreen()

            if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.changeTile]")
        }

        /**
         * Method for removing entities that are under cursor when creating new entity on that space
         *
         * @param skipGround if is true, this method will skip checking for ground and player position
         */
        private fun removeEntity(skipGround: Boolean): Boolean {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.removeEntity]")

            // Getting coordinates
            val position = Position(Data.Player.position)

            // Checking, if we could place it on the ground
            val tile: ImageBitmap = graphics.getTile(position) ?: return false

            if (!skipGround) {
                // Ground
                if (collisions.checkForCollision(tile) == collisions.immovable) return true
                // PLAYER
                if (Data.LevelEditor.holdPosition.equals(position)) return true
            }

            // Checking, if it collides with an interactive thing
            // If yes, then remove it
            val toRemove = ArrayList<Interactive>()
            for (i in Data.Map.interactive.indices) {
                val interPosition = Data.Map.interactive[i].position
                if (interPosition.equals(position)) toRemove.add(Data.Map.interactive[i])
            }
            for (inter in toRemove) {
                Data.Map.interactive.remove(inter)
            }

            // Removing old listener
            listeners.removeRefreshListener(Data.Player.position)

            return false
        }

        /**
         * Checking, if we get error if we want to interact with nonexistent space in a map.
         *
         * @return JSONArray of row in which the cursor is
         */
        private fun checkForShift(): ArrayList<String> {
            if (Debug.Map.LEVEL_EDITOR) println(">>> [LevelEditor.checkForShift]")

            // Checking if the cursor is in the negative of the map
            val shift = Position()
            // Y
            if (Data.Player.position.y < 0) {
                shift.y = Data.Player.position.y
            }
            // X
            if (Data.Player.position.x < 0) {
                shift.x = Data.Player.position.x
            }
            // Shifting, if we need to
            if (shift.y != 0 || shift.x != 0) mapUtils.shiftMap(shift)
            // Trying to get row, which cursor is in

            // Retrieve the row array at index y
            val rowArray = try {
                Data.map!![Data.Player.position.y]
            } catch (e: IndexOutOfBoundsException) {
                // In case of failure, just create a new one
                ArrayList()
            }

            if (Debug.Map.LEVEL_EDITOR) println("<<< [LevelEditor.checkForShift]")
            return rowArray
        }

        /**
         * Saving everything
         */
        private fun save() {
            if (Debug.Map.LEVEL_EDITOR) println("--- [LevelEditor.save]")

            saveSettings()
            saveMap()

            val text = TextData()
            text.position = Position((Data.Player.radius - 1) * Data.IMAGE_SCALE * Data.IMAGE_SIZE, 0)
            text.text = "The map was saved."
            text.isCentered = true

            graphics.drawTextField(text)
        }
    }
}
