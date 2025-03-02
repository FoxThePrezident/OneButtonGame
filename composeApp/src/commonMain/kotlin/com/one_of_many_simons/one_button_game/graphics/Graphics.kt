package com.one_of_many_simons.one_button_game.graphics

import com.one_of_many_simons.one_button_game.dataClasses.Position

/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Graphics {
    fun initMap()
    fun resizeScreen()
    fun refreshScreen()
    fun clearLayer(layer: Int)
    fun revalidate()
    fun getTile(position: Position): ByteArray?
    fun drawTile(position: Position, tile: ByteArray?, layer: Int)
    fun drawText(textField: Text)
    fun showTextInput()

    companion object {
        val GROUND_LAYER: Int
        val ENTITIES_LAYER: Int
        val DECOR_LAYER: Int
        val PLAYER_LAYER: Int
        val TEXT_LAYER: Int
        val ARROW_LAYER: Int

        fun create(): Graphics
    }
}