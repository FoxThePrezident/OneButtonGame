package com.one_of_many_simons.one_button_game.graphics

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.listeners
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.TextInput
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.listeners.PlayerMoveListener
import com.one_of_many_simons.one_button_game.listeners.TextInputListener
import java.awt.Dimension
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import javax.swing.*


/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Graphics {
    private val layersCount = 6

    /**
     * Size of an image after scaling
     */
    private val imageSize = Data.IMAGE_SIZE * Data.IMAGE_SCALE

    /**
     * Method for initialization screen.
     */
    actual fun initMap() {
        if (Debug.Graphics.GRAPHICS) println(">>> [Graphics.initMap]")

        if (frame != null) {
            frame!!.dispose()
        }
        frame = JFrame()

        // General window settings
        frame!!.title = "One button game"
        frame!!.defaultCloseOperation =
            JFrame.EXIT_ON_CLOSE
        frame!!.isVisible = true
        frame!!.isResizable = false

        // Panel used for storing drawing panels
        layeredPane = JLayeredPane()
        frame!!.contentPane.add(layeredPane)

        // Initializing panels for drawing
        panels = ArrayList(layersCount)
        for (i in 0..<layersCount) {
            val panel = JPanel()
            // Setting transparency
            panel.isOpaque = false
            panel.layout = null

            // Add each panel to the JLayeredPane with its layer index
            layeredPane!!.add(panel, i)

            panels!!.add(panel)
        }

        resizeScreen()

        // LISTENERS
        frame!!.addKeyListener(PlayerMoveListener())
        frame!!.addMouseListener(PlayerMoveListener())

        if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.initMap]")
    }

    /**
     * Resizing screen and centering it.
     */
    actual fun resizeScreen() {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.resizeScreen]")

        // Dimensions of the window
        val gridSize = Data.Player.radius * 2 + 1
        val windowWidth = gridSize * imageSize
        val windowHeight = gridSize * imageSize
        val halfTile = imageSize / Data.IMAGE_SCALE
        frame!!.setSize(windowWidth + halfTile - 1, windowHeight + halfTile * 2 + 5)
        frame!!.setLocationRelativeTo(null)
        layeredPane!!.preferredSize =
            Dimension(windowWidth, windowHeight)

        // Setting maximum size for panels
        for (panel in panels!!) {
            panel.setBounds(0, 0, windowWidth, windowHeight)
        }
    }

    /**
     * Method for refreshing screen.
     */
    actual fun refreshScreen() {
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
                if (tile != null) {
                    drawTile(Position(x, y), tile, GROUND_LAYER)
                }
            }
        }

        // Notifying entities that screen got refreshed
        listeners.callRefreshListeners()

        if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.refreshScreen]")
    }

    /**
     * Clearing everything inside given layer
     *
     * @param layer that we want to remove
     */
    actual fun clearLayer(layer: Int) {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.removeLayer]")

        // Removing content from panels
        panels!![layer].removeAll()
        revalidate()
    }

    /**
     * Revalidating panels on screen
     */
    actual fun revalidate() {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.revalidate]")

        for (i in 0..<layersCount) {
            panels!![i].revalidate()
            panels!![i].repaint()
        }

        // Redrawing layeredPane
        layeredPane!!.revalidate()
        layeredPane!!.repaint()
    }

    /**
     * Getting tile from a map on certain position.
     *
     * @param position of tile, we want to get
     * @return ByteArray on specified position
     */
    actual fun getTile(position: Position): ByteArray? {
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

    /**
     * Drawing tile on the screen.
     *
     * @param position of tile, we want to place
     * @param tile     that will be drawn
     * @param layer    which layer we want to draw on
     */
    actual fun drawTile(position: Position, tile: ByteArray?, layer: Int) {
        if (Debug.Graphics.GRAPHICS) println(">>> [Graphics.drawTile]")

        if (tile == null) {
            if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.drawTile] tile is null")
            return
        }

        // PLAYER position
        val playerY = Data.Player.position.y
        val playerX = Data.Player.position.x

        // Starting position
        val startY = playerY - Data.Player.radius
        val startX = playerX - Data.Player.radius

        // Adjusting coordinate based on player position
        val pixelY = (position.y - startY) * imageSize
        val pixelX = (position.x - startX) * imageSize

        // Drawing tile
        val icon: ImageIcon? = byteArrayToImageIcon(tile)

        val label = JLabel(icon)
        label.setBounds(pixelX, pixelY, imageSize, imageSize)
        panels!![layer].add(label)

        if (Debug.Graphics.GRAPHICS) println("<<< [Graphics.drawTile]")
    }

    /**
     * Converting ByteArray to ImageIcon
     * @param imageData is PNG image, that we want to convert
     * @return converted ImageIcon
     */
    private fun byteArrayToImageIcon(imageData: ByteArray): ImageIcon? {
        return try {
            val inputStream = ByteArrayInputStream(imageData)
            val bufferedImage = ImageIO.read(inputStream)
            ImageIcon(bufferedImage)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Drawing text field on screen
     *
     * @param textField that will be drawn
     */
    actual fun drawText(textField: Text) {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.drawText]")

        panels!![TEXT_LAYER].add(
            textField.getText(
                frame!!.width
            ) as JLabel
        )
        panels!![TEXT_LAYER].repaint()
    }

    /**
     * Showing text input for things like signs.
     */
    actual fun showTextInput() {
        if (Debug.Graphics.GRAPHICS) println("--- [Graphics.showTextInput]")

        TextInput.open(TextInputListener())
    }

    actual companion object {
        // Constants
        actual const val GROUND_LAYER: Int = 5
        actual const val ENTITIES_LAYER: Int = 4
        actual const val DECOR_LAYER: Int = 3
        actual const val PLAYER_LAYER: Int = 2
        actual const val TEXT_LAYER: Int = 1
        actual const val ARROW_LAYER: Int = 0

        /**
         * LAUNCHER window
         */
        private var frame: JFrame? = null

        /**
         * Window for graphics
         */
        private var layeredPane: JLayeredPane? = null

        /**
         * Panels for displaying things
         */
        private var panels: ArrayList<JPanel>? = null

        /**
         * Clearing the whole screen.
         */
        private fun clearScreen() {
            if (Debug.Graphics.GRAPHICS) println("--- [Graphics.clearScreen]")

            for (panel in panels!!) {
                panel.removeAll()
                panel.revalidate()
                panel.repaint()
            }
            layeredPane!!.revalidate()
            layeredPane!!.repaint()
        }

        actual fun create(): Graphics = Graphics()
    }
}
