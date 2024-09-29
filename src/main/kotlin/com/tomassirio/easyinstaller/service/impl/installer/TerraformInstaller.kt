package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.CloudCLITool
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@CloudCLITool
class TerraformInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext,
        private val osArchUtil: OSArchUtil
) : InstallableApplication {

    @Value("\${url.default.terraform}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Terraform"

    private fun createDefaultCommand(): String {
        val os = osArchUtil.getOS()
                .replace("MacOSX", "darwin")
                .replace("Windows", "windows")
                .replace("Linux", "linux")
        val arch = osArchUtil.getArch()
                .replace("x86_64", "amd64")

        val finalURL = DEFAULT_URL.replace("{OS}", os).replace("{ARCH}", arch)

        return DefaultCommandBuilder(name(), finalURL)
                .setFileName("terraform.zip")
                .setExtractCommand("unzip")
                .addPostExtractCommands(
                        "sudo mv terraform /usr/local/bin/")
                .addCleanupCommands(
                        "rm terraform.zip")
                .useSudo()
                .build()
    }
}