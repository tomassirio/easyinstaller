package com.tomassirio.easyinstaller.command

import com.tomassirio.easyinstaller.command.step.PackageManagerStep
import com.tomassirio.easyinstaller.command.step.ShellAndTerminalManagerStep
import com.tomassirio.easyinstaller.command.step.VersionControlSystemStep
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.shell.command.annotation.Command

@Command(command = ["install"], description = "Install applications")
class InstallerCommand(
    private val installerService: ApplicationInstallerService,
    private val packageManagerStep: PackageManagerStep,
    private val shellAndTerminalManagerStep: ShellAndTerminalManagerStep,
    private val versionControlSystemStep: VersionControlSystemStep,
    private val shellFormatter: ShellFormatter
) {

    @Command(command = ["-q"], alias = ["--quick"], description = "Quick install preferred applications")
    fun quickInstall() {
        installerService.installAllInOrder()
    }

    @Command(command = ["-m"], alias = ["--manual"], description = "Install applications manually")
    fun installManually() {
        packageManagerStep.execute()
        val shellAndTerminalApps = shellAndTerminalManagerStep.execute()
        val versionControlSystemApps = versionControlSystemStep.execute()

        listOf(
            shellAndTerminalApps,
            versionControlSystemApps
        )
            .flatten()
            .forEach {
                runCatching {
                    shellFormatter.printWarning("Application $it is going to be installed")
                    installerService.installApplication(it)
                }.onFailure {
                    shellFormatter.printError("Application $it not found")
                }
            }
    }
}
