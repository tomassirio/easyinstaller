package com.tomassirio.easyinstaller.service

interface InstallableApplication {
    fun name(): String
    fun install(packageManager: String? = null)
}