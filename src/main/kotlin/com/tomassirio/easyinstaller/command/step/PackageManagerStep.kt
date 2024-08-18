package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.annotation.PackageManager
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.service.impl.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.SelectItem
import org.springframework.stereotype.Component

@Component
class PackageManagerStep(
    private val componentFlowBuilder: ComponentFlow.Builder,
    applicationContext: ApplicationContext,
    private val applicationInstallerService: ApplicationInstallerService,
    private val downloadStrategyContext: DownloadStrategyContext,
    private val shellFormatter: ShellFormatter,
    environment: Environment
) : BaseStep(applicationContext, environment), InstallationStep {

    override fun execute(): List<String> {
        val flow = componentFlowBuilder.clone().reset()
            .withSingleItemSelector("selectedApp")
            .name("Package Manager to Install")
            .selectItems(getInstallers(PackageManager::class.java).map { app ->
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

        confirmationStep(selectedApp)
        return emptyList()
    }

    private fun confirmationStep(selectedApp: String?) {
        if (!selectedApp.isNullOrEmpty()) {
            val confirmationFlow = componentFlowBuilder.clone().reset()
                .withConfirmationInput("confirmDefaultPackageManager")
                .name("Do you want to set $selectedApp as default package manager? (Recommended)")
                .defaultValue(true)
                .and()
                .build()

            val confirmationResult = confirmationFlow.run()

            val confirmDefault = confirmationResult.context.get<Boolean>("confirmDefaultPackageManager") ?: true

            if (confirmDefault) {
                downloadStrategyContext.setCurrentStrategyByName(selectedApp)
            }
        }
    }
}
