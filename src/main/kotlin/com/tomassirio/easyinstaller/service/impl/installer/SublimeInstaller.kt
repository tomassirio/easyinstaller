package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.IdesAndTextEditor
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@IdesAndTextEditor
class SublimeInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext
) : InstallableApplication {

    @Value("\${url.default.sublime}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Sublime"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .setFileName("sublime_text_3")
                .setExtractCommand(" tar -xz")
                .addPostExtractCommands(
                        "cd sublime_text_3",
                        "sudo mv sublime_text /usr/bin/sublime",
                        "sudo ln -s /usr/bin/sublime /usr/bin/subl"
                )
                .addCleanupCommands(
                        "cd ..",
                        "rm -rf sublime_text_3"
                )
                .useSudo()
                .build()
    }
}