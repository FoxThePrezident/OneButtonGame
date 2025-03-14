package com.one_of_many_simons.one_button_game.graphics

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.R

/**
 * Desktop implementation of Icons
 */
@Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Icons {
    actual object Player {
        @JvmField
        actual val player: ImageBitmap = fileHandle.loadIcon(R.drawable.player)

        @JvmField
        actual val up: ImageBitmap = fileHandle.loadIcon(R.drawable.up)

        @JvmField
        actual val down: ImageBitmap = fileHandle.loadIcon(R.drawable.down)

        @JvmField
        actual val left: ImageBitmap = fileHandle.loadIcon(R.drawable.left)

        @JvmField
        actual val right: ImageBitmap = fileHandle.loadIcon(R.drawable.right)
    }

    actual object Enemies {
        @JvmField
        actual val zombie: ImageBitmap = fileHandle.loadIcon(R.drawable.zombie)
    }

    actual object Interactive {
        @JvmField
        actual val hp_potion: ImageBitmap = fileHandle.loadIcon(R.drawable.hp_potion)

        @JvmField
        actual val sign: ImageBitmap = fileHandle.loadIcon(R.drawable.sign)
    }

    actual object Environment {
        @JvmField
        actual val blank: ImageBitmap = fileHandle.loadIcon(R.drawable.empty)

        @JvmField
        actual val wall: ImageBitmap = fileHandle.loadIcon(R.drawable.wall)

        @JvmField
        actual val floor: ImageBitmap = fileHandle.loadIcon(R.drawable.floor)
    }

    actual object LevelEditor {
        @JvmField
        actual val cursor: ImageBitmap = fileHandle.loadIcon(R.drawable.cursor)
    }

    actual object General {
        @JvmField
        actual val inventory: ImageBitmap = fileHandle.loadIcon(R.drawable.inventory)

        @JvmField
        actual val menu: ImageBitmap = fileHandle.loadIcon(R.drawable.menu)

        @JvmField
        actual val move: ImageBitmap = fileHandle.loadIcon(R.drawable.move)

        @JvmField
        actual val attention: ImageBitmap = fileHandle.loadIcon(R.drawable.attention)
    }
}