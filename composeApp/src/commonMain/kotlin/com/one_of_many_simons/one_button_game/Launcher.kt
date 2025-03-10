package com.one_of_many_simons.one_button_game

import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.map.LevelEditor
import com.one_of_many_simons.one_button_game.menu.Menu

/**
 * LAUNCHER class
 */
class Launcher {
    /**
     * Initializing game
     */
    fun init() {
        if (Debug.LAUNCHER) println(">>> [Launcher.init]")

        // Initializing
        Data.Libraries.graphics.init()
        Data.Libraries.fileHandle.initFiles()
        Data.Libraries.playerActions.init()
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
            Data.Libraries.listeners.addRefreshListener(player!!)

            // Starting thread for changing player actions
            if (!Data.LevelEditor.levelEdit) {
                val playerThread = Thread(player)
                playerThread.start()
            }

            Data.Libraries.graphics.refreshScreen()
            if (Debug.LAUNCHER) println("<<< [Launcher.createPlayer]")
        }
    }
}