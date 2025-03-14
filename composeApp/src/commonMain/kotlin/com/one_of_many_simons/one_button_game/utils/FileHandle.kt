package com.one_of_many_simons.one_button_game.utils

/**
 * Handling file related stuff, like loading and saving text and images.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FileHandle {
    fun initFiles()
    fun loadText(fileName: String, fromJar: Boolean): String?
    fun saveText(fileName: String, content: String)
    fun getContentOfDirectory(directory: String): Array<String?>

    companion object {
        operator fun invoke(): FileHandle
    }
}