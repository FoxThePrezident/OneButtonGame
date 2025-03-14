package com.one_of_many_simons.one_button_game.menu

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.loadInteractive
import com.one_of_many_simons.one_button_game.Data.loadMap
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.Launcher
import com.one_of_many_simons.one_button_game.Launcher.Companion.createPlayer
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.textInputListeners
import com.one_of_many_simons.one_button_game.dataClasses.MenuItem
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ARROW_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.newMapListener
import com.one_of_many_simons.one_button_game.map.LevelEditor
import kotlin.system.exitProcess

/**
 * Contains all methods that are used in menus
 */
class MenuCommands(menu: Menu?) {
    private val newMapName = "New map"

    init {
        Companion.menu = menu
    }

    /**
     * Loading main menu
     */
    fun main_menu() {
        if (Debug.Menu.MENU_COMMANDS) println(">>> [MenuCommands.main_menu]")

        val menuItems = ArrayList<MenuItem>()
        Menu.generateMenu("MainMenu", menuItems)

        if (Debug.Menu.MENU_COMMANDS) println("<<< [MenuCommands.main_menu]")
    }

    /**
     * Create new game with player
     */
    fun newGame() {
        if (Debug.Menu.MENU_COMMANDS) println(">>> [MenuCommands.newGame]")

        val maps: Array<String?> = fileHandle.getContentOfDirectory("maps")
        val menuItems = ArrayList<MenuItem>()

        // Looping over every entry of map and creating object for each one
        for (map in maps) {
            val mapObject = MenuItem()

            val mapName = map!!.replace(".json", "")

            mapObject.label = mapName
            mapObject.itemType = "command"
            mapObject.action = "generateNewGame"
            mapObject.parameters = mapName

            val visible = ArrayList<String>()
            visible.add("NewGame")
            mapObject.visible = visible

            menuItems.add(mapObject)
        }

        Menu.generateMenu("NewGame", menuItems)

        if (Debug.Menu.MENU_COMMANDS) println("<<< [MenuCommands.newGame]")
    }

    /**
     * Generate new game for player
     *
     * @param mapName that will be loaded
     */
    fun generateNewGame(mapName: String) {
        if (Debug.Menu.MENU_COMMANDS) println(">>> [MenuCommands.generateNewGame]")

        // Clearing old things.
        listeners.clearListeners()

        // Loading new ones.
        Data.Map.currentMap = mapName
        loadMap()
        loadInteractive()

        menu!!.running = false
        Data.running = true

        createPlayer()

        if (Debug.Menu.MENU_COMMANDS) println("<<< [MenuCommands.generateNewGame]")
    }

    /**
     * Generate new menu with options for level editing
     */
    fun levelEditor() {
        if (Debug.Menu.MENU_COMMANDS) println(">>> [MenuCommands.LEVEL_EDITOR]")

        // Getting maps that could be edited
        var maps: Array<String?> = fileHandle.getContentOfDirectory("maps")

        maps = maps.copyOf(maps.size + 1)
        maps[maps.size - 1] = newMapName

        val menuItems = ArrayList<MenuItem>()

        // Looping over every entry of map and creating object for each one
        for (map in maps) {
            val mapObject = MenuItem()

            val mapName = map!!.replace(".json", "")

            mapObject.label = mapName
            mapObject.itemType = "command"
            mapObject.action = "generateNewLevelEdit"
            mapObject.parameters = mapName

            val visible = ArrayList<String>()
            visible.add("NewGame")
            mapObject.visible = visible

            menuItems.add(mapObject)
        }

        Menu.generateMenu("NewGame", menuItems)

        if (Debug.Menu.MENU_COMMANDS) println("<<< [MenuCommands.LEVEL_EDITOR]")
    }

    /**
     * Create new game in level edit mode
     *
     * @param mapName which map will be loaded
     */
    fun newMapLevelEdit(mapName: String) {
        if (Debug.Menu.MENU_COMMANDS) println(">>> [MenuCommands.newMapLevelEdit]")

        menu!!.running = false
        Launcher.levelEditor = LevelEditor()

        if (mapName == newMapName) {
            textInputListeners.show { newMapListener() }
            return
        } else {
            Data.Map.currentMap = mapName
        }

        // Saving meanwhile position for player
        Data.LevelEditor.holdPosition = Position(Data.Player.position)
        Data.LevelEditor.levelEdit = true

        // Clearing old things.
        listeners.clearListeners()

        // Loading new ones.
        loadMap()
        loadInteractive()
        listeners.addRefreshListener(Launcher.levelEditor!!)

        createPlayer()

        graphics.refreshScreen()
        Icons.LevelEditor.cursor.let { graphics.drawTile(Data.Player.position, it, ARROW_LAYER) }
        graphics.trigger()

        if (Debug.Menu.MENU_COMMANDS) println("<<< [MenuCommands.newMapLevelEdit]")
    }

    /**
     * Resuming game
     */
    fun resumeGame() {
        if (Debug.Menu.MENU_COMMANDS) println(">>> [MenuCommands.resumeGame]")

        listeners.removeRefreshListener(menu!!, true)

        menu!!.running = false

        graphics.refreshScreen()

        Data.running = true
        val player = Thread(Launcher.player)
        player.start()

        if (Debug.Menu.MENU_COMMANDS) println("<<< [MenuCommands.resumeGame]")
    }

    /**
     * Method for exiting game
     */
    fun exitGame() {
        if (Debug.Menu.MENU_COMMANDS) println("--- [MenuCommands.exitGame]")
        menu!!.running = false
        exitProcess(0)
    }

    companion object {
        @JvmField
        var menu: Menu? = null
    }
}
