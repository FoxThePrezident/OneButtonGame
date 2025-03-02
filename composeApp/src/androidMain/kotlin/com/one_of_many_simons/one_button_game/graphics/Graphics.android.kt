package com.one_of_many_simons.one_button_game.graphics

import com.one_of_many_simons.one_button_game.dataClasses.Position

/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Graphics {
    actual fun initMap() {
        // TODO
    }

    actual fun resizeScreen() {
        // TODO
    }

    actual fun refreshScreen() {
        // TODO
    }

    actual fun clearLayer(layer: Int) {
        // TODO
    }

    actual fun revalidate() {
        // TODO
    }

    actual fun getTile(position: Position): ByteArray? {
        // TODO
        return null
    }

    actual fun drawTile(position: Position, tile: ByteArray?, layer: Int) {
        // TODO
    }

    actual fun drawText(textField: Text) {
        // TODO
    }

    actual fun showTextInput() {
        // TODO
    }

    actual companion object {
        actual val GROUND_LAYER: Int = -1 // TODO
        actual val ENTITIES_LAYER: Int = -1 // TODO
        actual val DECOR_LAYER: Int = -1 // TODO
        actual val PLAYER_LAYER: Int = -1 // TODO
        actual val TEXT_LAYER: Int = -1 // TODO
        actual val ARROW_LAYER: Int = -1 // TODO

        actual fun create(): Graphics {
            // TODO
            return Graphics()
        }
    }
}