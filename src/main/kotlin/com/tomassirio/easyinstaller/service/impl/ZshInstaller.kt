package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.annotation.ShellAndTerminalManager
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.impl.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@ShellAndTerminalManager
class ZshInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
): InstallableApplication {

    @Value("\${command.default.zsh}")
    lateinit var DEFAULT_COMMAND: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) DEFAULT_COMMAND else name().lowercase()
        strategy(command)
    }

    override fun name() = "Zsh"
}