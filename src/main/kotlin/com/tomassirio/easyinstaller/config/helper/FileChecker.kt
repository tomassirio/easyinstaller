package com.tomassirio.easyinstaller.config.helper

interface FileChecker {
    fun fileExists(path: String): Boolean
}