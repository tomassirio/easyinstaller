package com.tomassirio.easyinstaller.command

import com.tomassirio.easyinstaller.command.step.PacketManagerStep
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import org.springframework.shell.command.annotation.Command

@Command(command = ["install"], description = "Install applications")
class InstallerCommand(
    private val installerService: ApplicationInstallerService,
    private val packetManagerStep: PacketManagerStep,
) {

    @Command(command = ["-q", "--quick"], description = "Quick install preferred applications")
    fun quickInstall() {
        installerService.installAllInOrder()
    }

    @Command(command = ["-m", "--manual"], description = "Install applications manually")
    fun installManually() {
        packetManagerStep.execute()
    }
}
