package com.one_of_many_simons.one_button_game

import com.google.gson.Gson
import com.one_of_many_simons.one_button_game.entities.player.PlayerActions
import com.one_of_many_simons.one_button_game.graphics.Graphics
import com.one_of_many_simons.one_button_game.listeners.Listeners
import com.one_of_many_simons.one_button_game.listeners.TextInputListener
import com.one_of_many_simons.one_button_game.map.Collisions
import com.one_of_many_simons.one_button_game.menu.Menu
import com.one_of_many_simons.one_button_game.menu.MenuCommands
import com.one_of_many_simons.one_button_game.utils.FileHandle
import com.one_of_many_simons.one_button_game.utils.MapUtils

/**
 * Global variables to reduce memory usage by creating multiple instances of these classes
 */
object Libraries {
    lateinit var gson: Gson
    lateinit var menu: Menu
    lateinit var mapUtils: MapUtils
    lateinit var graphics: Graphics
    lateinit var listeners: Listeners
    lateinit var collisions: Collisions
    lateinit var fileHandle: FileHandle
    lateinit var menuCommands: MenuCommands
    lateinit var playerActions: PlayerActions
    lateinit var textInputListeners: TextInputListener
}