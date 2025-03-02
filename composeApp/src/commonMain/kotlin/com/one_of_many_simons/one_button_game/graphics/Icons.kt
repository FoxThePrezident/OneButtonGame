package com.one_of_many_simons.one_button_game.graphics

import com.one_of_many_simons.one_button_game.Data.Libraries.fileHandle

/**
 * Icons for graphics.
 */
@Suppress("unused")
class Icons {
    /**
     * PLAYER related icons.
     */
    object Player {
        /**
         * PLAYER icon.
         */
        @JvmField
        val player: ByteArray? = fileHandle.loadIcon("/image/player/player.png")

        // Arrows
        @JvmField
        val up: ByteArray? = fileHandle.loadIcon("/image/player/up.png")

        @JvmField
        val down: ByteArray? = fileHandle.loadIcon("/image/player/down.png")

        @JvmField
        val left: ByteArray? = fileHandle.loadIcon("/image/player/left.png")

        @JvmField
        val right: ByteArray? = fileHandle.loadIcon("/image/player/right.png")
    }

    /**
     * Enemies related icons.
     */
    object Enemies {
        @JvmField
        val zombie: ByteArray? = fileHandle.loadIcon("/image/entities/zombie.png")
    }

    /**
     * Interactive related icons.
     */
    object Interactive {
        @JvmField
        val hp_potion: ByteArray? = fileHandle.loadIcon("/image/interactive/HP_potion.png")

        @JvmField
        val sign: ByteArray? = fileHandle.loadIcon("/image/interactive/sign.png")
    }

    /**
     * Environment. Like ground, void, walls...
     */
    object Environment {
        @JvmField
        val blank: ByteArray? = fileHandle.loadIcon("/image/environment/void.png")

        @JvmField
        val wall: ByteArray? = fileHandle.loadIcon("/image/environment/wall.png")

        @JvmField
        val floor: ByteArray? = fileHandle.loadIcon("/image/environment/floor.png")
    }

    /**
     * Icons for level editor.
     */
    object LevelEditor {
        @JvmField
        val cursor: ByteArray? = fileHandle.loadIcon("/image/cursor.png")
    }

    /**
     * MENU and general icons
     */
    object General {
        @JvmField
        val inventory: ByteArray? = fileHandle.loadIcon("/image/icons/inventory.png")

        @JvmField
        val menu: ByteArray? = fileHandle.loadIcon("/image/icons/menu.png")

        @JvmField
        val move: ByteArray? = fileHandle.loadIcon("/image/icons/move.png")
    }
}
