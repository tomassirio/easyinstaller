package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
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
        private val downloadStrategyContext: DownloadStrategyContext,
        private val osArchUtil: OSArchUtil
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
        val os = osArchUtil.getOS()

        val finalURL = when (os) {
            "MacOSX" ->  DEFAULT_URL.replace("{OS}", "mac")
            "Windows" -> DEFAULT_URL.replace("{OS}", "winx-64")
            else -> throw IllegalStateException("OS not supported")
        }

        return DefaultCommandBuilder(name(), finalURL)
                .setFileName("sublime_text")
                .setExtractCommand("unzip sublime_text.zip")
                .addPostExtractCommands("sudo mv sublime_text /usr/bin/sublime")
                .useSudo()
                .build()
    }
}