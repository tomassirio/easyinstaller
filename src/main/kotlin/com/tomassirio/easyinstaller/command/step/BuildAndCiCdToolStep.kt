package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.annotation.BuildAndCiCdTool
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.SelectItem
import org.springframework.stereotype.Component

@Component
class BuildAndCiCdToolStep(
    private val componentFlowBuilder: ComponentFlow.Builder,
    applicationContext: ApplicationContext,
    environment: Environment
) : BaseStep(applicationContext, environment), InstallationStep {

    override fun execute(): List<String> {
        val flow = componentFlowBuilder.clone().reset()
            .withMultiItemSelector("selectedApps")
            .name("Build and CI/CD Tools to Install")
            .selectItems(getInstallers(BuildAndCiCdTool::class.java).map { app ->
                SelectItem.of(app.name(), app.name())
            })
            .and()
            .build()

        val result = flow.run()

        return result.context.get<List<String>>("selectedApps") ?: emptyList()
    }
}