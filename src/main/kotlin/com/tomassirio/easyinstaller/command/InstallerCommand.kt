package com.tomassirio.easyinstaller.command

import com.tomassirio.easyinstaller.command.step.PackageManagerStep
import com.tomassirio.easyinstaller.command.step.ShellAndTerminalManagerStep
import com.tomassirio.easyinstaller.command.step.VersionControlSystemStep
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import org.springframework.shell.command.annotation.Command

@Command(command = ["install"], description = "Install applications")
class InstallerCommand(
    private val installerService: ApplicationInstallerService,
    private val packageManagerStep: PackageManagerStep,
    private val shellAndTerminalManagerStep: ShellAndTerminalManagerStep,
    private val versionControlSystemStep: VersionControlSystemStep
) {

    @Command(command = ["-q"], alias = ["--quick"], description = "Quick install preferred applications")
    fun quickInstall() {
        installerService.installAllInOrder()
    }

    @Command(command = ["-m"], alias = ["--manual"], description = "Install applications manually")
    fun installManually() {
        val packageManager = packageManagerStep.execute()
        // add a step to set the package manager if desired
        shellAndTerminalManagerStep.execute(packageManager)
        versionControlSystemStep.execute(packageManager)
    }
}
