package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.annotation.PackageManager
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.context.ApplicationContext
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.SelectItem
import org.springframework.stereotype.Component

@Component
class PackageManagerStep(
    private val componentFlowBuilder: ComponentFlow.Builder,
    applicationContext: ApplicationContext,
    private val applicationInstallerService: ApplicationInstallerService,
    private val shellFormatter: ShellFormatter
) : BaseStep(applicationContext), InstallationStep {

    override fun execute(packageName: String?): String? {
        val flow = componentFlowBuilder.clone().reset()
            .withSingleItemSelector("selectedApp")
            .name("Select Applications to Install")
            .selectItems(getInstallers<PackageManager>().map { app ->
                SelectItem.of(app.name(), app.name())
            })
            .and()
            .build()

        val result = flow.run()

        val selectedApp = result.context.get<String>("selectedApp") ?: null

        selectedApp?.let {name ->
            shellFormatter.printWarning("Application $name is going to be installed")
            try {
                applicationInstallerService.installApplication(name)
            } catch (e: IllegalArgumentException) {
                shellFormatter.printError("Application $name not found")
            }
        }
        return selectedApp
    }
}
