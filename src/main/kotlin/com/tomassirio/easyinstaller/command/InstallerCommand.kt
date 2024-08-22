package com.tomassirio.easyinstaller.command

import com.tomassirio.easyinstaller.command.step.*
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.shell.command.annotation.Command

@Command(command = ["install"], description = "Install applications")
class InstallerCommand(
    private val installerService: ApplicationInstallerService,
    private val packageManagerStep: PackageManagerStep,
    private val shellAndTerminalManagerStep: ShellAndTerminalManagerStep,
    private val versionControlSystemStep: VersionControlSystemStep,
    private val commandLineToolStep: CommandLineToolStep,
    private val programmingLanguageToolStep: ProgrammingLanguageToolStep,
    private val databaseToolStep: DatabaseToolStep,
    private val containersAndVirtualizationStep: ContainerAndVirtualizationToolStep,
    private val cloudCLIToolsStep: CloudCliToolStep,
    private val securityToolsStep: SecurityToolStep,
    private val communicationToolStep: CommunicationToolStep,
    private val documentationToolsStep: DocumentationToolStep,
    private val buildAndCICDToolsStep: BuildAndCiCdToolStep,
    private val backupAndSyncToolsStep: BackupSyncToolStep,
    private val shellFormatter: ShellFormatter
) {

    @Command(command = ["-q"], alias = ["--quick"], description = "Quick install preferred applications")
    fun quickInstall() {
        installerService.installAllInOrder()
    }

    @Command(command = ["-m"], alias = ["--manual"], description = "Install applications manually")
    fun installManually() {
        // Run Package Manager First and set default package manager
        packageManagerStep.execute()

        listOf(
            shellAndTerminalManagerStep.execute(),
            versionControlSystemStep.execute(),
            commandLineToolStep.execute(),
            programmingLanguageToolStep.execute(),
            databaseToolStep.execute(),
            containersAndVirtualizationStep.execute(),
            cloudCLIToolsStep.execute(),
            securityToolsStep.execute(),
            communicationToolStep.execute(),
            documentationToolsStep.execute(),
            buildAndCICDToolsStep.execute(),
            backupAndSyncToolsStep.execute()
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
