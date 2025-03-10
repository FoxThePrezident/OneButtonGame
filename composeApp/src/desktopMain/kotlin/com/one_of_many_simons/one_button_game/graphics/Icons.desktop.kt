package com.one_of_many_simons.one_button_game.graphics

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data.Libraries.fileHandle

/**
 * Desktop implementation of Icons
 */
@Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Icons {
    actual object Player {
        @JvmField
        actual val player: ImageBitmap? = fileHandle.loadIcon("/image/player/player.png")

        @JvmField
        actual val up: ImageBitmap? = fileHandle.loadIcon("/image/player/up.png")

        @JvmField
        actual val down: ImageBitmap? = fileHandle.loadIcon("/image/player/down.png")

        @JvmField
        actual val left: ImageBitmap? = fileHandle.loadIcon("/image/player/left.png")

        @JvmField
        actual val right: ImageBitmap? = fileHandle.loadIcon("/image/player/right.png")
    }

    actual object Enemies {
        @JvmField
        actual val zombie: ImageBitmap? = fileHandle.loadIcon("/image/entities/zombie.png")
    }

    actual object Interactive {
        @JvmField
        actual val hp_potion: ImageBitmap? = fileHandle.loadIcon("/image/interactive/HP_potion.png")

        @JvmField
        actual val sign: ImageBitmap? = fileHandle.loadIcon("/image/interactive/sign.png")
    }

    actual object Environment {
        @JvmField
        actual val blank: ImageBitmap? = fileHandle.loadIcon("/image/environment/void.png")

        @JvmField
        actual val wall: ImageBitmap? = fileHandle.loadIcon("/image/environment/wall.png")

        @JvmField
        actual val floor: ImageBitmap? = fileHandle.loadIcon("/image/environment/floor.png")
    }

    actual object LevelEditor {
        @JvmField
        actual val cursor: ImageBitmap? = fileHandle.loadIcon("/image/cursor.png")
    }

    actual object General {
        @JvmField
        actual val inventory: ImageBitmap? = fileHandle.loadIcon("/image/icons/inventory.png")

        @JvmField
        actual val menu: ImageBitmap? = fileHandle.loadIcon("/image/icons/menu.png")

        @JvmField
        actual val move: ImageBitmap? = fileHandle.loadIcon("/image/icons/move.png")
    }
}
