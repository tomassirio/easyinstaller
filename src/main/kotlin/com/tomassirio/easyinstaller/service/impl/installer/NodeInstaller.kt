package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.ProgrammingLanguageTool
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Service

@Service
@ProgrammingLanguageTool
class NodeInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
): InstallableApplication {
    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) null else name().lowercase()
        strategy(command)
    }

    override fun name() = "Node"
}