package com.one_of_many_simons.one_button_game.graphics

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Icons for graphics.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object Icons {
    object Player {
        val player: ImageBitmap
        val up: ImageBitmap
        val down: ImageBitmap
        val left: ImageBitmap
        val right: ImageBitmap
    }

    object Enemies {
        val zombie: ImageBitmap
    }

    object Interactive {
        val hp_potion: ImageBitmap
        val sign: ImageBitmap
    }

    object Environment {
        val blank: ImageBitmap
        val wall: ImageBitmap
        val floor: ImageBitmap
    }

    object LevelEditor {
        val cursor: ImageBitmap
    }

    object General {
        val inventory: ImageBitmap
        val menu: ImageBitmap
        val move: ImageBitmap
        val attention: ImageBitmap
    }
}
