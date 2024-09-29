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
class VsCodeInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext,
        private val osArchUtil: OSArchUtil
) : InstallableApplication {

    @Value("\${url.default.vscode}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "VsCode"

    private fun createDefaultCommand(): String {
        val os = osArchUtil.getOS()
        val arch = osArchUtil.getArch()
                .replace("x86_64", "x64")

        val finalURL = when (os) {
            "MacOSX" -> DEFAULT_URL.replace("{OS}", "darwin").replace("{ARCH}", arch)
            "Linux" -> DEFAULT_URL.replace("{OS}", "alpine").replace("{ARCH}", arch)
            "Windows" -> DEFAULT_URL.replace("{OS}", "win32").replace("{ARCH}", arch)
            else -> throw IllegalArgumentException("Unsupported OS")
        }

        return DefaultCommandBuilder(name(), finalURL)
                .setFileName("vscode")
                .setExtractCommand("unzip")
                .addPostExtractCommands(
                        "cd vscode",
                        "./configure",
                        "make",
                        "sudo make install")
                .addCleanupCommands(
                        "cd ..",
                        "rm -rf vscode")
                .useSudo()
                .build()
    }
}