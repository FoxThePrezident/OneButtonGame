package com.one_of_many_simons.one_button_game.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Graphics.GRAPHICS
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.EXCEPTION
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.textInputListeners
import com.one_of_many_simons.one_button_game.dataClasses.Global
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.dataClasses.TextData
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.map.LevelEditor.Companion.move

/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
class Graphics {
    private lateinit var focusRequester: FocusRequester

    /**
     * Initializing map
     */
    fun init() {
        debug(GRAPHICS, CORE, "--- [Graphics.initMap]")

        repeat(NUM_LAYERS) {
            layers.add(mutableListOf())
        }
    }

    /**
     * Triggering UI recomposition
     */
    fun trigger() {
        debug(GRAPHICS, INFORMATION, "--- [Graphics.trigger]")

        trigger.value++
    }

    /**
     * Refreshing whole screen
     */
    fun refreshScreen() {
        debug(GRAPHICS, CORE, ">>> [Graphics.refreshScreen]")

        // Clearing previous content of the screen
        clearScreen()

        // Creating variables
        val gridSize = Data.Player.radius * 2 + 1
        val playerY = Data.Player.position.y
        val playerX = Data.Player.position.x
        val startY = playerY - Data.Player.radius
        val startX = playerX - Data.Player.radius

        // Looping over each tile around the player
        for (y in startY..<startY + gridSize) {
            for (x in startX..<startX + gridSize) {
                val tile = getTile(Position(x, y))
                drawTile(Position(x, y), tile, GROUND_LAYER)
            }
        }

        // Notifying entities that screen got refreshed
        listeners.callRefreshListeners()

        debug(GRAPHICS, CORE, "<<< [Graphics.refreshScreen]")
    }

    /**
     * Clearing whole layer
     * @param layer which layer should be cleaned
     */
    fun clearLayer(layer: Int) {
        debug(GRAPHICS, CORE, "--- [Graphics.clearLayer]")

        layers[layer].clear()
    }

    /**
     * Getter for tile
     * @param position from which we want a tile
     */
    fun getTile(position: Position): ImageBitmap {
        debug(GRAPHICS, CORE, "--- [Graphics.getTile]")

        try {
            val tileName = Data.map?.getOrNull(position.y)?.getOrNull(position.x) ?: ""
            return when (tileName) {
                "W" -> Icons.Environment.wall
                " " -> Icons.Environment.floor
                else -> Icons.Environment.blank
            }
        } catch (e: IndexOutOfBoundsException) {
            debug(GRAPHICS, EXCEPTION, "--- [Graphics.getTile] IndexOutOfBoundsException ${e.printStackTrace()}")
            return Icons.Environment.blank
        }
    }

    fun drawTile(position: Position, tile: ImageBitmap?, layer: Int) {
        debug(GRAPHICS, CORE, ">>> [Graphics.drawTile]")

        if (tile == null) {
            debug(GRAPHICS, EXCEPTION, "<<< [Graphics.drawTile] Tile is null")
            return
        }

        // PLAYER position
        val playerX = Data.Player.position.x
        val playerY = Data.Player.position.y
        val playerRadius = Data.Player.radius

        // Starting position, top left
        val startX = playerX - playerRadius
        val startY = playerY - playerRadius

        // Adjusting coordinate based on player position
        val pixelX = ((position.x - startX) * IMAGE_SIZE).toFloat()
        val pixelY = ((position.y - startY) * IMAGE_SIZE).toFloat()

        layers[layer].add {
            drawImage(tile, topLeft = Offset(pixelX, pixelY))
        }

        debug(GRAPHICS, CORE, "<<< [Graphics.drawTile]")
    }

    fun drawTextField(textField: TextData) {
        debug(GRAPHICS, CORE, ">>> [Graphics.drawTextField]")

        layers[TEXT_LAYER].add {
            val textMeasurer = Global.textMeasurer

            val textLayout = textMeasurer.measure(
                text = AnnotatedString(
                    textField.text,
                    spanStyle = SpanStyle(color = textField.textColor)
                )
            )

            val textWidth = textLayout.size.width.toFloat()
            val textHeight = textLayout.size.height.toFloat()
            val padding = 10f
            val backgroundWidth = textWidth + padding * 2
            val backgroundHeight = textHeight + padding * 2

            val posX = textField.position.x.toFloat()
            val posY = textField.position.y.toFloat()

            val textStartX = if (textField.isCentered) {
                (size.width - backgroundWidth) / 2 + padding
            } else {
                posX + padding
            }

            val textStartY = posY + padding

            // Background
            if (textField.backgroundColor != null) {
                drawRect(
                    color = textField.backgroundColor!!,
                    topLeft = Offset(if (textField.isCentered) 0f else posX, posY),
                    size = if (textField.isCentered)
                        Size(size.width, backgroundHeight)
                    else
                        Size(backgroundWidth, backgroundHeight)
                )

            }

            // Border
            if (textField.borderColor != null) {
                drawRect(
                    color = textField.borderColor!!,
                    topLeft = Offset(if (textField.isCentered) 0f else posX, posY),
                    size = if (textField.isCentered)
                        Size(size.width, backgroundHeight)
                    else
                        Size(backgroundWidth, backgroundHeight),
                    style = Stroke(width = textField.borderWidth.toPx())
                )
            }

            // Text
            drawText(
                textMeasurer = textMeasurer,
                text = buildAnnotatedString {
                    withStyle(ParagraphStyle(textAlign = TextAlign.Start)) {
                        append(textField.text)
                    }
                },
                style = TextStyle(color = textField.textColor),
                topLeft = Offset(textStartX, textStartY)
            )
        }

        debug(GRAPHICS, CORE, "<<< [Graphics.drawTextField]")
    }

    private fun clearScreen() {
        debug(GRAPHICS, CORE, "--- [Graphics.clearScreen]")

        layers.forEach { it.clear() }
    }

    fun getFocus(): FocusRequester {
        return focusRequester
    }

    @Composable
    fun render() {
        debug(GRAPHICS, CORE, "--- [Graphics.render]")

        Canvas(modifier = getModifier()) {
            for (layer in layers) {
                layer.forEach { drawAction ->
                    drawAction(this)
                }
            }

            // Using trigger to force update
            if (trigger.value > 0) print("")
        }

        textInputListeners.getTextInput()
    }

    @Composable
    private fun getModifier(): Modifier {
        debug(GRAPHICS, INFORMATION, "--- [Graphics.getModifier]")

        focusRequester = FocusRequester()

        // Request focus when the composable is first composed
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        return Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable() // Ensures keyboard input is captured
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (textInputListeners.getVisibility()) return@detectTapGestures
                        if (!Data.LevelEditor.levelEdit) {
                            Player.action()
                        }
                    },
                    onLongPress = { println("Long Pressed at ($it)") }
                )
            }
            .onKeyEvent { event ->
                when (event.type) {
                    KeyEventType.KeyDown -> {
                        if (keyPressed) return@onKeyEvent true
                        keyPressed = true

                        if (Data.LevelEditor.levelEdit) {
                            move(event.key)
                        } else {
                            Player.action()
                        }

                        true // Consume the event
                    }

                    KeyEventType.KeyUp -> {
                        keyPressed = false
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
    }

    companion object {
        const val GROUND_LAYER: Int = 0
        const val ENTITIES_LAYER: Int = 1
        const val PLAYER_LAYER: Int = 2
        const val DECOR_LAYER: Int = 3
        const val TEXT_LAYER: Int = 4
        const val ARROW_LAYER: Int = 5
        private const val NUM_LAYERS: Int = 6
        private val layers = mutableListOf<MutableList<DrawScope.() -> Unit>>()

        private const val IMAGE_SIZE = Data.IMAGE_SIZE * Data.IMAGE_SCALE

        private var keyPressed: Boolean = false

        // Workaround variable, that triggers recomposition of UI
        private var trigger = mutableIntStateOf(0)
    }
}