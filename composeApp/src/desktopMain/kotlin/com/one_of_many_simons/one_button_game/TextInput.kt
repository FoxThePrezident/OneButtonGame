package com.one_of_many_simons.one_button_game

import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Window for text inputs, allowing text input for certain aspects of the game, like creating signs or generating new level.
 */
class TextInput {
    companion object {
        private var frame: JFrame? = null
        private var textField: JTextField? = null

        /**
         * Open text input
         *
         * @param listener is what will be called when user entered text
         */
        @JvmStatic
        fun open(listener: ActionListener?) {
            if (Debug.TEXT_INPUT) println(">>> [TextInput.open]")

            // Create a new frame to store text field and button
            frame = JFrame("Text input")
            frame!!.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame!!.setLocationRelativeTo(null)
            frame!!.isResizable = false
            frame!!.isVisible = true

            // Create a new button
            val submitBtn = JButton("submit")
            submitBtn.addActionListener(listener)

            // Create an object of JTextField with 16 columns
            textField = JTextField(16)
            textField!!.addActionListener(listener)

            // Create a panel to add buttons and text field
            val panel = JPanel()

            // Adding things to a screen
            panel.add(textField)
            panel.add(submitBtn)
            frame!!.add(panel)
            frame!!.pack()

            if (Debug.TEXT_INPUT) println("<<< [TextInput.open]")
        }

        /**
         * Deleting text input window to be able to reuse
         */
        fun dispose() {
            if (Debug.TEXT_INPUT) println("--- [TextInput.dispose]")
            if (frame != null) {
                frame!!.dispose()
            }
        }

        /**
         * Getter for currently inputted text.
         *
         * @return text field text
         */
        fun getText(): String {
            if (Debug.TEXT_INPUT) println("--- [TextInput.getText]")
            return textField!!.text
        }
    }
}
