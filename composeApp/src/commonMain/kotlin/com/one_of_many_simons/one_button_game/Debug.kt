package com.one_of_many_simons.one_button_game

/**
 * For debugging application and getting quickly what is when called.<br>
 * Prints open and close statements in a function, like:
 *
 * `>>> [Launcher.init]`
 *
 * `<<< [Launcher.init]`
 *
 * Useful, if you need to know what and when is called, without using ides debugger.
 */
object Debug {
    /**
     * Flags for functions. Their order is same as in file system for easier navigation.
     */
    object Flags {
        object Entities {
            object Enemies {
                const val ZOMBIE: Int = 0
            }

            object Player {
                const val ITEM: Int = 0
                const val PLAYER: Int = 0
                const val PLAYER_ACTIONS: Int = 0
            }

            object Potions {
                const val HP: Int = 0
            }

            object Templates {
                const val ENEMY: Int = 0
                const val POTION: Int = 0
                const val SIGN: Int = 0
            }
        }

        object Graphics {
            const val GRAPHICS: Int = 0
        }

        object Listeners {
            const val ADD_SIGN: Int = 0
            const val LISTENERS: Int = 0
            const val NEW_MAP_LISTENER: Int = 0
            const val TEXT_INPUT_LISTENER: Int = 0
        }

        object Map {
            const val COLLISIONS: Int = 0
            const val LEVEL_EDITOR: Int = 0
        }

        object Menu {
            const val MENU: Int = 0
            const val MENU_COMMANDS: Int = 0
        }

        object Utils {
            const val FILE_UTILS: Int = 0
            const val MAP_UTILS: Int = 0
        }

        const val DATA: Int = 0
        const val LAUNCHER: Int = 0
    }

    /**
     * Debug levels
     * lower level means more critical the message is
     */
    object Levels {
        /**
         * Used for critical bugs that occurs in code
         */
        const val EXCEPTION: Int = 0

        /**
         * Critical things like initialization, refresh screen
         */
        const val CORE: Int = 1

        /**
         * Not relevant, but useful information like setters and getters
         */
        const val INFORMATION: Int = 2
    }

    /**
     * Debug function for handling debug messages.
     *
     * Message format: `prefix [class.function name] additional information`<br>
     * Example: `>>> [Launcher.init] Hello world!`<br>
     * Prefixes for messages:
     *  *  `>>>` for entering a function, placed at the start
     *  *  `<<<` for exiting a function, placed at the end
     *  *  `---` information inside a function, or where first two indicators are not needed, like getters and setters or short functions
     *
     * @param flag is from `Debug.Flags` object
     * @param level that message is, defined in `Debug.Levels` object
     * @param message that will be printed in case that debug level us sufficient
     *
     */
    fun debug(flag: Int, level: Int, message: String) {
        if (flag >= level) {
            println(message)
        }
    }
}
