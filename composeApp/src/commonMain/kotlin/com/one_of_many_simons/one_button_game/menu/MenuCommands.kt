package com.one_of_many_simons.one_button_game.menu

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.loadInteractive
import com.one_of_many_simons.one_button_game.Data.loadMap
import com.one_of_many_simons.one_button_game.Debug.Flags.Menu.MENU_COMMANDS
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Launcher
import com.one_of_many_simons.one_button_game.Launcher.Companion.createPlayer
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.textInputListeners
import com.one_of_many_simons.one_button_game.dataClasses.MenuItem
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ACTIONS_LAYER
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
        debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.main_menu]")

        val menuItems = ArrayList<MenuItem>()
        Menu.generateMenu("MainMenu", menuItems)

        debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.main_menu]")
    }

    /**
     * Create new game with player
     */
    fun newGame() {
        debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.newGame]")

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

        debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.newGame]")
    }

    /**
     * Generate new game for player
     *
     * @param mapName that will be loaded
     */
    fun generateNewGame(mapName: String) {
        debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.generateNewGame]")

        // Clearing old things.
        listeners.clearListeners()

        // Loading new ones.
        Data.Map.currentMap = mapName
        loadMap()
        loadInteractive()

        menu!!.running = false
        Data.running = true

        createPlayer()

        debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.generateNewGame]")
    }

    /**
     * Generate new menu with options for level editing
     */
    fun levelEditor() {
        debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.LEVEL_EDITOR]")

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

        debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.LEVEL_EDITOR]")
    }

    /**
     * Create new game in level edit mode
     *
     * @param mapName which map will be loaded
     */
    fun newMapLevelEdit(mapName: String) {
        debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.newMapLevelEdit]")

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
        Icons.LevelEditor.cursor.let { graphics.drawTile(Data.Player.position, it, ACTIONS_LAYER) }
        graphics.trigger()

        debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.newMapLevelEdit]")
    }

    /**
     * Resuming game
     */
    fun resumeGame() {
        debug(MENU_COMMANDS, CORE, ">>> [MenuCommands.resumeGame]")

        listeners.removeRefreshListener(menu!!, true)

        menu!!.running = false

        graphics.refreshScreen()

        Data.running = true
        val player = Thread(Launcher.player)
        player.start()

        debug(MENU_COMMANDS, CORE, "<<< [MenuCommands.resumeGame]")
    }

    /**
     * Method for exiting game
     */
    fun exitGame() {
        debug(MENU_COMMANDS, CORE, "--- [MenuCommands.exitGame]")

        menu!!.running = false
        exitProcess(0)
    }

    companion object {
        @JvmField
        var menu: Menu? = null
    }
}
