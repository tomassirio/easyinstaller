package com.tomassirio.easyinstaller.config.helper

interface SystemPropertyWrapper {
    fun getProperty(key: String): String?
}