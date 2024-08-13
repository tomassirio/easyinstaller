package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.InstallableApplication

interface InstallationStep {
    fun execute()

    val installers: List<InstallableApplication>
}