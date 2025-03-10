package com.one_of_many_simons.one_button_game

/**
 * For debugging application.
 * Prints open and close statements in a function, like:
 * <pre>`>>> [Launcher.init]
 * <<< [Launcher.init]
`</pre> *
 *
 *
 * Useful, if you need to know what and when is called, without using ides debugger.
 * Format: indicator [class.function name] additional information.<br></br>
 * Example: `>>> [Launcher.init] Hello world!`
 *
 *
 * Indicators:
 *
 *  *  `>>>` for entering a function, placed at the start
 *  *  `<<<` for exiting a function, placed at the end
 *  *  `---` information inside a function, or where first two indicators are not needed, like getters and setters
 *
 * <br></br>
 * Order of these boolean variables are same as in file system for easier finding.
 */
object Debug {
    const val DATA: Boolean = false
    const val LAUNCHER: Boolean = false

    class Entities {
        object Enemies {
            const val ZOMBIE: Boolean = false
        }

        object Player {
            const val ITEM: Boolean = false
            const val PLAYER: Boolean = false
            const val PLAYER_ACTIONS: Boolean = false
        }

        object Potions {
            const val HP: Boolean = false
        }

        object Templates {
            const val ENEMY: Boolean = false
            const val POTION: Boolean = false
            const val SIGN: Boolean = false
        }
    }

    object Graphics {
        const val GRAPHICS: Boolean = false
    }

    object Listeners {
        const val LISTENERS: Boolean = false
        const val TEXT_INPUT_LISTENER: Boolean = false
    }

    object Map {
        const val COLLISIONS: Boolean = false
        const val LEVEL_EDITOR: Boolean = false
    }

    object Menu {
        const val MENU: Boolean = false
        const val MENU_COMMANDS: Boolean = false
    }

    object Utils {
        const val FILE_UTILS: Boolean = false
        const val MAP_UTILS: Boolean = false
    }
}
