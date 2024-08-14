package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.annotation.ShellProvider
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Service

@Service
@ShellProvider
class ZshInstaller(private val shellFormatter: ShellFormatter): InstallableApplication {
    override fun install() {
        shellFormatter.printInfo("Installing Zsh...")
        // Shell command to install Zsh could go here
    }

    override fun name() = "Zsh"
}