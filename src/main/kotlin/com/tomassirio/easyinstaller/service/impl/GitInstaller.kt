package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.annotation.DevelopmentTool
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Service

@Service
@DevelopmentTool
class GitInstaller(private val shellFormatter: ShellFormatter) : InstallableApplication {
    override fun install() {
        shellFormatter.printInfo("Installing Git...")
        // Shell command to install Git could go here
    }

    override fun name() = "Git"
}