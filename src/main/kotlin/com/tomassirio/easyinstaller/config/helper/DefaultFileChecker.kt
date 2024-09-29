package com.tomassirio.easyinstaller.config.helper

import java.io.File

class DefaultFileChecker : FileChecker {
    override fun fileExists(path: String): Boolean {
        return File(path).exists()
    }
}