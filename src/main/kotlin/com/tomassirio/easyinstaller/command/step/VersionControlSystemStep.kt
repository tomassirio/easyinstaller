package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.service.annotation.VersionControlSystem
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.context.ApplicationContext
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.SelectItem
import org.springframework.stereotype.Component

@Component
class VersionControlSystemStep(
    private val componentFlowBuilder: ComponentFlow.Builder,
    applicationContext: ApplicationContext,
    private val applicationInstallerService: ApplicationInstallerService,
    private val shellFormatter: ShellFormatter
) : BaseStep(applicationContext), InstallationStep{

    override fun execute(packageName: String?): String? {
        val flow = componentFlowBuilder.clone().reset()
            .withMultiItemSelector("selectedApps")
            .name("Select Applications to Install")
            .selectItems(getInstallers<VersionControlSystem>().map { app ->
                SelectItem.of(app.name(), app.name())
            })
            .and()
            .build()

        val result = flow.run()

        val selectedNames = result.context.get<List<String>>("selectedApps") ?: emptyList()

        selectedNames.forEach { name ->
            shellFormatter.printWarning("Application $name is going to be installed")
            try {
                applicationInstallerService.installApplication(name, packageName)
            } catch (e: IllegalArgumentException) {
                shellFormatter.printError("Application $name not found")
            }
        }
        return null
    }
}