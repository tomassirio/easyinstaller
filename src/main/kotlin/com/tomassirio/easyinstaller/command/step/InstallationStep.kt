package com.tomassirio.easyinstaller.command.step

interface InstallationStep {
    fun execute(packageName: String? = null): String?
}