package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.CommandLineTool
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@CommandLineTool
class HttpieInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext
) : InstallableApplication {

    @Value("\${url.default.httpie}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Httpie"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .addPostExtractCommands(
                        "sudo gpg --dearmor -o /usr/share/keyrings/httpie-archive-keyring.gpg",
                        "echo deb [arch=amd64 signed-by=/usr/share/keyrings/httpie-archive-keyring.gpg] https://packages.httpie.io/deb ./ | sudo tee /etc/apt/sources.list.d/httpie.list > /dev/null",
                        "sudo apt update",
                        "sudo apt install httpie"
                )
                .useSudo()
                .build()
    }
}