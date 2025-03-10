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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.listeners
import com.one_of_many_simons.one_button_game.Debug
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
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.initMap]")

        repeat(NUM_LAYERS) {
            layers.add(mutableListOf())
        }
    }

    /**
     * Triggering UI recomposition
     */
    fun trigger() {
        if (Debug.Graphics.GRAPHICS) println(">>> [Graphics.trigger]")
        trigger.value++
    }

    /**
     * Refreshing whole screen
     */
    fun refreshScreen() {
        if (Debug.Graphics.GRAPHICS) println(">>> [Graphics.refreshScreen]")

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

        if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.refreshScreen]")
    }

    /**
     * Clearing whole layer
     * @param layer which layer should be cleaned
     */
    fun clearLayer(layer: Int) {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.clearLayer]")

        layers[layer].clear()
    }

    /**
     * Getter for tile
     * @param position from which we want a tile
     */
    fun getTile(position: Position): ImageBitmap? {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.getTile]")

        try {
            val tileName = Data.map?.getOrNull(position.y)?.getOrNull(position.x) ?: ""
            return when (tileName) {
                "W" -> Icons.Environment.wall
                " " -> Icons.Environment.floor
                else -> Icons.Environment.blank
            }
        } catch (e: IndexOutOfBoundsException) {
            return Icons.Environment.blank
        }
    }

    fun drawTile(position: Position, tile: ImageBitmap?, layer: Int) {
        if (Debug.Graphics.GRAPHICS) println(">>> [Graphics.drawTile]")

        if (tile == null) {
            if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.drawTile] Tile is null")
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

        if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.drawTile]")
    }

    fun drawTextField(textField: TextData) {
        if (Debug.Graphics.GRAPHICS) println(">>> [Graphics.drawTextField]")

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

        if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.drawTextField]")
    }

    private fun clearScreen() {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.clearScreen]")
        layers.forEach { it.clear() }
    }

    fun getFocus():FocusRequester {
        return focusRequester
    }

    @Composable
    fun render() {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.render]")

        Canvas(modifier = getModifier()) {
            for (layer in layers) {
                layer.forEach { drawAction ->
                    drawAction(this)
                }
            }

            // Using trigger to force update
            if (trigger.value > 0) print("")
        }

        Data.Libraries.textInputListeners.getTextInput()
    }

    @Composable
    private fun getModifier(): Modifier {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.getModifier]")


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
        const val DECOR_LAYER: Int = 2
        const val PLAYER_LAYER: Int = 3
        const val TEXT_LAYER: Int = 4
        const val ARROW_LAYER: Int = 5
        private const val NUM_LAYERS: Int = 6
        private val layers = mutableListOf<MutableList<DrawScope.() -> Unit>>()

        private const val IMAGE_SIZE = Data.IMAGE_SIZE * Data.IMAGE_SCALE

        private var keyPressed: Boolean = false

        // Workaround variable, that triggers recomposition of UI
        private var trigger = mutableIntStateOf(0)

        fun create(): Graphics {
            return Graphics()
        }
    }
}