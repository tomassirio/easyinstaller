package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.annotation.PacketManager
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.context.ApplicationContext
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.SelectItem
import org.springframework.stereotype.Component

@Component
class PacketManagerStep(
    private val componentFlowBuilder: ComponentFlow.Builder,
    override val applicationContext: ApplicationContext,
    private val applicationInstallerService: ApplicationInstallerService,
    private val shellFormatter: ShellFormatter
) : BaseStep(applicationContext), InstallationStep{

    override fun execute() {
        val flow = componentFlowBuilder.clone().reset()
            .withMultiItemSelector("selectedApps")
            .name("Select Applications to Install")
            .selectItems(getInstallers<PacketManager>().map { app ->
                SelectItem.of(app.name(), app.name())
            })
            .and()
            .build()

        val result = flow.run()

        val selectedNames = result.context.get<List<String>>("selectedApps") ?: emptyList()

        selectedNames.forEach { name ->
            shellFormatter.printWarning("Application $name is going to be installed")
            try {
                applicationInstallerService.installApplication(name)
                shellFormatter.printSuccess("Application $name installed successfully")
            } catch (e: IllegalArgumentException) {
                shellFormatter.printError("Application $name not found")
            }
        }
    }
}