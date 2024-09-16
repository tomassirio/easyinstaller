package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.ShellAndTerminalManager
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@ShellAndTerminalManager
class AlacrittyInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
): InstallableApplication {

    @Value("\${url.default.alacritty}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Alacritty"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .setFileName("Alacritty-v0.13.0-x86_64.tar.gz")
                .addPostExtractCommands(
                        "cd Alacritty-v0.13.0-x86_64",
                        "./install.sh"
                )
                .addCleanupCommands(
                        "cd ..",
                        "rm -rf Alacritty-v0.13.0-x86_64",
                        "rm Alacritty-v0.13.0-x86_64.tar.gz"
                )
                .useSudo()
                .build()
    }
}