package com.one_of_many_simons.one_button_game.menu

import androidx.compose.ui.graphics.Color
import com.google.gson.reflect.TypeToken
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.gson
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.menuCommands
import com.one_of_many_simons.one_button_game.dataClasses.MenuItem
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.dataClasses.TextData
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.TEXT_LAYER
import com.one_of_many_simons.one_button_game.listeners.RefreshListener
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * LAUNCHER class for handling menu actions, like loading menu, changing or activating
 */
class Menu : Runnable, RefreshListener {
    var running: Boolean = true

    /**
     * Initializing map
     */
    fun init() {
        if (Debug.Menu.MENU) println(">>> [Menu.init]")

        menuCommands = MenuCommands(this)
        menuItems = ArrayList()

        loadMenu()
        listeners.addRefreshListener(this)

        if (Debug.Menu.MENU) println("<<< [Menu.init]")
    }

    /**
     * Open new menu
     *
     * @param newMenu that will be opened
     */
    fun setMenu(newMenu: String) {
        if (Debug.Menu.MENU) println("--- [Menu.setMenu]")

        currentMenu = newMenu
        running = true

        init()

        val thread = Thread(this)
        thread.start()
    }

    /**
     * Main loop for changing menu items
     */
    override fun run() {
        if (Debug.Menu.MENU) println(">>> [Menu.run]")

        drawMenuItems()
        while (running) {
            drawMenuItems()

            try {
                // Delay for controlling the loop speed
                Thread.sleep(Data.Player.controlDelay * 2L)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt() // restore interrupted status
            }
        }

        if (Debug.Menu.MENU) println("<<< [Menu.run]")
    }

    /**
     * Method for dynamically calling methods inside MENU_COMMANDS class
     *
     * @param action string of method, that needs to be called
     */
    @Throws(NoSuchMethodException::class)
    private fun executeAction(action: String, parameters: String) {
        if (Debug.Menu.MENU) println("--- [Menu.executeAction]")

        try {
            val method: Method
            if (parameters == "") {
                method = menuCommands.javaClass.getMethod(action)
                method.invoke(menuCommands)
            } else {
                method = menuCommands.javaClass.getMethod(
                    action,
                    String::class.java
                )
                method.invoke(menuCommands, parameters)
            }
        } catch (e: InvocationTargetException) {
            if (Debug.Menu.MENU) println("--- [Menu.executeAction] Exception")
            System.err.println("Failed to execute action: $action")
        } catch (e: IllegalAccessException) {
            if (Debug.Menu.MENU) println("--- [Menu.executeAction] Exception")
            System.err.println("Failed to execute action: $action")
        }
    }


    override fun onRefresh() {
        if (Debug.Menu.MENU) println("--- [Menu.onRefresh]")

        // Determine which menu item is currently selected
        val selectedIndex = borderList!!.indexOf(borderPrimary)
        if (selectedIndex != -1) {
            val selectedItem = menuItems!![selectedIndex]

            try {
                when (selectedItem.itemType) {
                    "command" -> {
                        val action = selectedItem.action
                        val parameters = selectedItem.parameters

                        val menuCommands = menuCommands
                        when (action) {
                            "main_menu" -> menuCommands.main_menu()
                            "newGame" -> menuCommands.newGame()
                            "generateNewGame" -> menuCommands.generateNewGame(parameters)
                            "resumeGame" -> menuCommands.resumeGame()
                            "levelEditor" -> menuCommands.levelEditor()
                            "generateNewLevelEdit" -> menuCommands.newMapLevelEdit(parameters)
                            "exitGame" -> menuCommands.exitGame()
                            else -> executeAction(action, parameters)
                        }
                    }

                    "menu" -> {
                        setMenu(selectedItem.label)
                    }
                }
            } catch (e: NoSuchMethodException) {
                if (Debug.Menu.MENU) println("--- [Menu.onRefresh] NoSuchMethodException")
                e.printStackTrace()
            }
        }
    }

    override fun getPosition(): Position? {
        return null
    }

    companion object {
        // Borders
        private val borderPrimary: Color = Color.Red
        private val borderSecondary: Color = Color.Black
        private var borderList: ArrayList<Color>? = null
        private var currentMenu = "MainMenu"
        private var menuItems: ArrayList<MenuItem>? = null

        /**
         * Loading menu from JSON
         */
        private fun loadMenu() {
            try {
                if (Debug.Menu.MENU) println(">>> [Menu.loadMenu]")
                // Load JSON file containing the menu
                val menuRaw: String = fileHandle.loadText("menu.json", false)!!
                val menu: ArrayList<MenuItem> =
                    gson.fromJson(menuRaw, object : TypeToken<ArrayList<MenuItem?>?>() {
                    }.type)

                for (menuItem in menu) {
                    val visible = menuItem.visible
                    for (s in visible) {
                        if (s == currentMenu) {
                            menuItems!!.add(menuItem)
                        }
                    }
                }

                generateBorders()

                if (Debug.Menu.MENU) println("<<< [Menu.loadMenu]")
            } catch (e: IOException) {
                if (Debug.Menu.MENU) println("<<< [Menu.loadMenu] IOException")
                throw RuntimeException(e)
            }
        }

        /**
         * Setting menu items, for example like new game
         *
         * @param currMenu     currently active menu
         * @param newMenuItems ArrayList containing data about item
         */
        fun generateMenu(currMenu: String, newMenuItems: ArrayList<MenuItem>?) {
            if (Debug.Menu.MENU) println(">>> [Menu.generateMenu]")

            currentMenu = currMenu
            menuItems = newMenuItems
            loadMenu()

            generateBorders()
            drawMenuItems()

            if (Debug.Menu.MENU) println("<<< [Menu.generateMenu]")
        }

        /**
         * Method for drawing menu items on screen
         */
        private fun drawMenuItems() {
            if (Debug.Menu.MENU) println(">>> [Menu.drawMenuItems]")

            // Shift borders in the borderList (move the last to the first position)
            val last = borderList!!.last()
            for (i in borderList!!.size - 2 downTo 0) {
                borderList!![i + 1] = borderList!![i]
            }
            borderList!![0] = last

            val startY = 50 // Starting Y position for the menu
            val paddingY = 50 // Vertical space between each menu item

            // Clear previous layer
            graphics.clearLayer(TEXT_LAYER)

            // Draw all menu items with their current borders
            for (i in menuItems!!.indices) {
                val menuItemText = menuItems!![i].label.replace("_", " ") // Format text
                val text = TextData()
                text.position = Position(64, startY + i * paddingY)
                text.text = menuItemText
                text.textColor = Color.White
                text.borderColor = borderList!![i]
                graphics.drawTextField(text)
            }

            graphics.trigger()

            if (Debug.Menu.MENU) println("<<< [Menu.drawMenuItems]")
        }

        /**
         * Generating borders based on number of items
         */
        private fun generateBorders() {
            if (Debug.Menu.MENU) println(">>> [Menu.generateBorders]")

            borderList = ArrayList()

            // Set initial position for drawing the text
            for (i in 0..<menuItems!!.size - 1) {
                borderList!!.add(borderSecondary)
            }
            borderList!!.add(borderPrimary)

            if (Debug.Menu.MENU) println("<<< [Menu.generateBorders]")
        }
    }
}
