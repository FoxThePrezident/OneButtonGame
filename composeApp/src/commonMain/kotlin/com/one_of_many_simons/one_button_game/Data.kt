package com.one_of_many_simons.one_button_game

import com.google.gson.Gson
import com.one_of_many_simons.one_button_game.dataClasses.*
import com.one_of_many_simons.one_button_game.entities.enemies.Zombie
import com.one_of_many_simons.one_button_game.entities.player.PlayerActions
import com.one_of_many_simons.one_button_game.entities.potions.HP
import com.one_of_many_simons.one_button_game.entities.templates.Sign
import com.one_of_many_simons.one_button_game.graphics.Graphics
import com.one_of_many_simons.one_button_game.listeners.Listeners
import com.one_of_many_simons.one_button_game.map.Collisions
import com.one_of_many_simons.one_button_game.menu.Menu
import com.one_of_many_simons.one_button_game.utils.FileHandle
import com.one_of_many_simons.one_button_game.utils.MapUtils
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
        if (Debug.DATA) println(">>> [Data.loadInteractive]")

        Map.enemyCount = 0

        val interactive = Map.interactive
        for (inter in interactive) {
            // Getting position of interactive thing
            val position = inter.position

            // Checking, which type it is
            when (inter.entityType) {
                "zombie" -> {
                    val zombie = Zombie(position)
                    Libraries.listeners.addRefreshListener(zombie)
                    Map.enemyCount++
                }

                "hp" -> {
                    val hp = HP(position)
                    Libraries.listeners.addRefreshListener(hp)
                }

                "sign" -> {
                    val signText = inter.text
                    val sign = Sign(position, signText)
                    Libraries.listeners.addRefreshListener(sign)
                }
            }
        }

        if (Debug.DATA) println("<<< [Data.loadInteractive]")
    }

    /**
     * Loading settings from a save file
     */
    fun loadSettings() {
        if (Debug.DATA) println(">>> [Data.loadSettings]")

        try {
            // Loading data for settings
            val settingsRaw = Libraries.fileHandle.loadText("settings.json", false)
            val settings = Libraries.gson.fromJson(settingsRaw, SettingsData::class.java)

            // Loading player related information
            val player = settings.player
            Player.controlDelay = player!!.controlDelay
        } catch (e: IOException) {
            if (Debug.DATA) println("<<< [Data.loadSettings] Exception")
            e.printStackTrace()
        }
        if (Debug.DATA) println("<<< [Data.loadSettings]")
    }

    /**
     * Loading map from a save file
     */
    @JvmStatic
    fun loadMap() {
        if (Debug.DATA) println(">>> [Data.loadMap]")

        try {
            // Loading data for map
            val mapName = "/maps/" + Map.currentMap + ".json"
            val mapRaw = Libraries.fileHandle.loadText(mapName, false)
                ?: throw RuntimeException("Cannot find $mapName")
            val levelData = Libraries.gson.fromJson(mapRaw, LevelData::class.java)

            // Loading map related information
            val map = levelData.map
            Map.walls = map.walls
            Map.ground = map.ground
            Map.interactive = map.interactive
            Data.map = Libraries.mapUtils.constructMap()

            // Loading player related information
            Player.position = levelData.player.position
        } catch (e: IOException) {
            if (Debug.DATA) println("<<< [Data.loadMap] Exception")
            e.printStackTrace()
        }
        if (Debug.DATA) println("<<< [Data.loadMap]")
    }

    /**
     * Saving settings to a file
     */
    @JvmStatic
    fun saveSettings() {
        if (Debug.DATA) println(">>> [Data.saveSettings]")

        // Storing player related information
        val data = SettingsData()
        val player = PlayerSettingsData()
        player.controlDelay = Player.controlDelay
        data.player = player

        // Saving data
        Libraries.fileHandle.saveText("/settings.json", Libraries.gson.toJson(data))

        if (Debug.DATA) println("<<< [Data.saveSettings]")
    }

    /**
     * Saving map to a file
     */
    @JvmStatic
    fun saveMap() {
        if (Debug.DATA) println(">>> [Data.saveMap]")

        // Trying to deconstruct a map to more manageable storing information
        Libraries.mapUtils.deconstructMap()

        // Storing map related information
        val levelData = LevelData()
        val mapData = MapData()
        mapData.walls = Map.walls
        mapData.ground = Map.ground
        mapData.interactive = Map.interactive
        levelData.map = mapData

        // Storing player related information
        val player = PlayerMapData()
        player.position = Position(LevelEditor.holdPosition)
        levelData.player = player

        // Saving data
        val mapName = "maps/" + Map.currentMap + ".json"
        Libraries.fileHandle.saveText(mapName, Libraries.gson.toJson(levelData))

        if (Debug.DATA) println("<<< [Data.saveMap]")
    }

    /**
     * Global variables to reduce memory usage by creating multiple instances of these classes
     */
    object Libraries {
        @JvmField
        var gson: Gson = Gson()

        @JvmField
        var menu: Menu = Menu()

        @JvmField
        var mapUtils: MapUtils = MapUtils()

        @JvmField
        var listeners: Listeners = Listeners()

        @JvmField
        var collisions: Collisions = Collisions()

        @JvmField
        var graphics: Graphics = Graphics.create()

        @JvmField
        var fileHandle: FileHandle = FileHandle.create()

        @JvmField
        var playerActions: PlayerActions = PlayerActions()
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
        var position: Position = Position()

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
