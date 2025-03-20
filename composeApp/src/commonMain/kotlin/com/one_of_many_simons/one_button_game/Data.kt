package com.one_of_many_simons.one_button_game

import com.one_of_many_simons.one_button_game.Debug.Flags.DATA
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.EXCEPTION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.gson
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.mapUtils
import com.one_of_many_simons.one_button_game.dataClasses.*
import com.one_of_many_simons.one_button_game.entities.enemies.Skeleton
import com.one_of_many_simons.one_button_game.entities.enemies.Zombie
import com.one_of_many_simons.one_button_game.entities.player.Armor
import com.one_of_many_simons.one_button_game.entities.potions.HP
import com.one_of_many_simons.one_button_game.entities.templates.Sign
import java.io.IOException

/**
 * Class for holding global game data
 */
object Data {
    /**
     * Scale, which images need to be resized
     */
    const val IMAGE_SCALE: Int = 3

    /**
     * Size of images in pixels
     */
    const val IMAGE_SIZE: Int = 16

    /**
     * JSON array storing current map.<br></br>
     * Storing each tile and its type.<br></br>
     * Formatted like 2D array, where each cell in nested one represents letter of that tile.<br></br>
     * Example:
     * <pre>`[
     * ["W", "W", "W"],
     * ["W", " ", "W"],
     * ["W", "W", "W"]
     * ]`</pre>
     */
    @JvmField
    var map: ArrayList<ArrayList<String>>? = null

    /**
     * LAUNCHER loop for player
     */
    @JvmField
    var running: Boolean = false

    /**
     * Loading interactive things from variable and creating new instances
     */
    @JvmStatic
    fun loadInteractive() {
        debug(DATA, CORE, ">>> [Data.loadInteractive]")

        Map.enemyCount = 0

        val interactive = Map.interactive
        for (inter in interactive) {
            // Getting position of interactive thing
            val position = inter.position

            // Checking, which type it is
            when (inter.entityType) {
                "zombie" -> {
                    val zombie = Zombie(position)
                    listeners.addRefreshListener(zombie)
                    Map.enemyCount++
                }

                "skeleton" -> {
                    val skeleton = Skeleton(position)
                    listeners.addRefreshListener(skeleton)
                    Map.enemyCount++
                }

                "hp" -> {
                    val hp = HP(position)
                    listeners.addRefreshListener(hp)
                }

                "sign" -> {
                    val signText = inter.text
                    val sign = Sign(position, signText)
                    listeners.addRefreshListener(sign)
                }

                "armor" -> {
                    val armor = Armor(position)
                    listeners.addRefreshListener(armor)
                }
            }
        }

        debug(DATA, CORE, "<<< [Data.loadInteractive]")
    }

    /**
     * Loading settings from a save file
     */
    fun loadSettings() {
        debug(DATA, CORE, ">>> [Data.loadSettings]")

        try {
            // Loading data for settings
            val settingsRaw = fileHandle.loadText("settings.json", false)
            val settings = gson.fromJson(settingsRaw, SettingsData::class.java)

            // Loading player related information
            val player = settings.player
            Player.controlDelay = player!!.controlDelay
        } catch (e: IOException) {
            debug(DATA, EXCEPTION, "<<< [Data.loadSettings] Exception: ${e.printStackTrace()}")
        }

        debug(DATA, CORE, "<<< [Data.loadSettings]")
    }

    /**
     * Loading map from a save file
     */
    @JvmStatic
    fun loadMap() {
        debug(DATA, CORE, ">>> [Data.loadMap]")

        try {
            // Loading data for map
            val mapName = "/maps/" + Map.currentMap + ".json"
            val mapRaw = fileHandle.loadText(mapName, false)
                ?: throw RuntimeException("Cannot find $mapName")
            val levelData = gson.fromJson(mapRaw, LevelData::class.java)

            // Loading map related information
            val map = levelData.map
            Map.walls = map.walls
            Map.ground = map.ground
            Map.interactive = map.interactive
            Data.map = mapUtils.constructMap()

            // Loading player related information
            Player.position = levelData.player.position
        } catch (e: IOException) {
            debug(DATA, EXCEPTION, "<<< [Data.loadMap] Exception: ${e.printStackTrace()}")
        }

        debug(DATA, CORE, "<<< [Data.loadMap]")
    }

    /**
     * Saving settings to a file
     */
    @JvmStatic
    fun saveSettings() {
        debug(DATA, CORE, ">>> [Data.saveSettings]")

        // Storing player related information
        val data = SettingsData()
        val player = PlayerSettingsData()
        player.controlDelay = Player.controlDelay
        data.player = player

        // Saving data
        fileHandle.saveText("/settings.json", gson.toJson(data))

        debug(DATA, CORE, "<<< [Data.saveSettings]")
    }

    /**
     * Saving map to a file
     */
    @JvmStatic
    fun saveMap() {
        debug(DATA, CORE, ">>> [Data.saveMap]")

        // Trying to deconstruct a map to more manageable storing information
        mapUtils.deconstructMap()

        // Storing map related information
        val levelData = LevelData()
        val mapData = MapData()
        mapData.walls = Map.walls
        mapData.ground = Map.ground
        mapData.interactive = Map.interactive
        levelData.map = mapData

        // Storing player related information
        val player = PlayerMapData()
        player.position = Position(LevelEditor.holdPosition.x, LevelEditor.holdPosition.y)
        levelData.player = player

        // Saving data
        val mapName = "maps/" + Map.currentMap + ".json"
        fileHandle.saveText(mapName, gson.toJson(levelData))

        debug(DATA, CORE, "<<< [Data.saveMap]")
    }

    /**
     * PLAYER related information.
     */
    object Player {
        /**
         * PLAYER current position.<br></br>
         * Formated like `y, x`
         */
        @JvmField
        var position: Position = Position(0, 0)

        /**
         * Players viewing radius.<br></br>
         * Tells, how many tiles around player are rendered.
         */
        @JvmField
        var radius: Int = 5

        /**
         * Delay in milliseconds between swapping action.
         */
        @JvmField
        var controlDelay: Int = 500
    }

    /**
     * Map and interactive things related stuff.
     */
    object Map {
        /**
         * Defining current map that is loaded.
         */
        @JvmField
        var currentMap: String = "first_level"

        /**
         * Position array for storing location of walls.<br></br>
         * Formated like `{Position(x, y), Position(x, y), ...}`
         */
        @JvmField
        var walls: ArrayList<Position> = ArrayList()

        /**
         * Position array for storing location ground tiles.<br></br>
         * Formated like `{Position(x, y), Position(x, y), ...}`
         */
        @JvmField
        var ground: ArrayList<Position> = ArrayList()

        /**
         * Array list of Interactive for storing interactive things like potions, enemies and signs.
         */
        @JvmField
        var interactive: ArrayList<Interactive> = ArrayList()

        /**
         * ENEMY count in level<br></br>
         * Used for determining winning condition.
         */
        @JvmField
        var enemyCount: Int = 0
    }

    /**
     * Things related to a level editor
     */
    object LevelEditor {
        /**
         * Unlocks ability to place things onto map.
         */
        @JvmField
        var levelEdit: Boolean = false

        /**
         * Hold position of player character.<br></br>
         * Formated like `Position(x, y)`
         */
        @JvmField
        var holdPosition: Position = Position()
    }
}
