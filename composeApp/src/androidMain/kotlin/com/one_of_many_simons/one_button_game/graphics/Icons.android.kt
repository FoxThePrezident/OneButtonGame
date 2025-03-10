package com.one_of_many_simons.one_button_game.graphics

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.R

/**
 * Desktop implementation of Icons
 */
@Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Icons {
    actual object Player {
        @JvmField
        actual val player: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.player)

        @JvmField
        actual val up: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.up)

        @JvmField
        actual val down: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.down)

        @JvmField
        actual val left: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.left)

        @JvmField
        actual val right: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.right)
    }

    actual object Enemies {
        @JvmField
        actual val zombie: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.zombie)
    }

    actual object Interactive {
        @JvmField
        actual val hp_potion: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.hp_potion)

        @JvmField
        actual val sign: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.sign)
    }

    actual object Environment {
        @JvmField
        actual val blank: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.empty)

        @JvmField
        actual val wall: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.wall)

        @JvmField
        actual val floor: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.floor)
    }

    actual object LevelEditor {
        @JvmField
        actual val cursor: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.cursor)
    }

    actual object General {
        @JvmField
        actual val inventory: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.inventory)

        @JvmField
        actual val menu: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.menu)

        @JvmField
        actual val move: ImageBitmap? = Data.Libraries.fileHandle.loadIcon(R.drawable.move)
    }
}