package com.one_of_many_simons.one_button_game

import com.google.gson.Gson
import com.one_of_many_simons.one_button_game.Libraries.gson
import com.one_of_many_simons.one_button_game.Libraries.menu
import com.one_of_many_simons.one_button_game.Libraries.mapUtils
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.collisions
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.playerActions
import com.one_of_many_simons.one_button_game.Libraries.textInputListeners
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.entities.player.PlayerActions
import com.one_of_many_simons.one_button_game.graphics.Graphics
import com.one_of_many_simons.one_button_game.listeners.Listeners
import com.one_of_many_simons.one_button_game.listeners.TextInputListener
import com.one_of_many_simons.one_button_game.map.Collisions
import com.one_of_many_simons.one_button_game.map.LevelEditor
import com.one_of_many_simons.one_button_game.menu.Menu
import com.one_of_many_simons.one_button_game.utils.MapUtils
import com.one_of_many_simons.one_button_game.utils.FileHandle

/**
 * Launching game
 */
class Launcher {
    /**
     * Initializing game
     */
    fun init() {
        if (Debug.LAUNCHER) println(">>> [Launcher.init]")

        // Initializing libraries
        gson = Gson()
        menu = Menu()
        mapUtils = MapUtils()
        graphics = Graphics()
        listeners = Listeners()
        fileHandle = FileHandle()
        collisions = Collisions()
        playerActions = PlayerActions()
        textInputListeners = TextInputListener()

        graphics.init()
        fileHandle.initFiles()
        playerActions.init()
        Data.loadSettings()
        Data.loadMap()

        // Loading interactive thing to a map
        Data.loadInteractive()
        createPlayer()

        // Creating menu
        val menu = Menu()
        menu.init()
        val menuThread = Thread(menu)
        menuThread.start()

        if (Debug.LAUNCHER) println("<<< [Launcher.init]")
    }

    companion object {
        @JvmField
        var player: Player? = null

        @JvmField
        var levelEditor: LevelEditor? = null

        @JvmStatic
        fun main(args: Array<String>) {
            if (Debug.LAUNCHER) println(">>> [Launcher.main]")

            // Initializing main components
            val launcher = Launcher()
            launcher.init()

            if (Debug.LAUNCHER) println("<<< [Launcher.main]")
        }

        /**
         * Creating new player instance
         */
        @JvmStatic
        fun createPlayer() {
            if (Debug.LAUNCHER) println(">>> [Launcher.createPlayer]")

            player = Player()
            listeners.addRefreshListener(player!!)

            // Starting thread for changing player actions
            if (!Data.LevelEditor.levelEdit) {
                val playerThread = Thread(player)
                playerThread.start()
            }

            graphics.refreshScreen()
            if (Debug.LAUNCHER) println("<<< [Launcher.createPlayer]")
        }
    }
}