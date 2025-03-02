package com.one_of_many_simons.one_button_game.utils

/**
 * Handling file related stuff, like loading and saving text and images.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileHandle {
    actual fun initFiles() {
        // TODO
    }

    actual fun loadText(fileName: String, fromJar: Boolean): String? {
        // TODO
        return ""
    }

    actual fun saveText(fileName: String, content: String) {
        // TODO
    }

    actual fun loadIcon(path: String): ByteArray? {
        // TODO
        return null
    }

    actual fun getContentOfDirectory(directory: String): Array<String?> {
        // TODO
        return emptyArray()
    }

    actual companion object {
        actual fun create(): FileHandle {
            // TODO
            return FileHandle()
        }
    }
}