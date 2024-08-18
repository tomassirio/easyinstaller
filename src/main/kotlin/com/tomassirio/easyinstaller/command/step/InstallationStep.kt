package com.tomassirio.easyinstaller.command.step

interface InstallationStep {
    fun execute(): List<String>
}