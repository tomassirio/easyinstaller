package com.tomassirio.easyinstaller.config.helper

class DefaultSystemPropertyWrapper : SystemPropertyWrapper {
    override fun getProperty(key: String): String? = System.getProperty(key)
}