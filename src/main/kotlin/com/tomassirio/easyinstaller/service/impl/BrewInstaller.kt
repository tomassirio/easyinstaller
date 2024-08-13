package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.annotation.PacketManager
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Service

@Service
@PacketManager
class BrewInstaller(private val shellFormatter: ShellFormatter) : InstallableApplication {
    override fun install() {
        shellFormatter.printInfo("Installing Brew...")
        // Shell command to install Brew could go here
    }

    override fun name() = "Brew"
}