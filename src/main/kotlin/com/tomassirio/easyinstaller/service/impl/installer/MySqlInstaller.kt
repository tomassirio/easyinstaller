package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.DatabaseTool
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@DatabaseTool
class MySqlInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext,
    private val osArchUtil: OSArchUtil
): InstallableApplication {

    @Value("\${url.default.mysql}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "MySql"

    private fun createDefaultCommand(): String {
        val os = osArchUtil.getOS()
        val arch = osArchUtil.getArch()

        if (os == "Unknown" || arch == "Unknown") {
            throw IllegalStateException("Unsupported OS or architecture")
        }


        val url = when (os ) {
            "Windows" -> DEFAULT_URL.replace("{OS}", "winx64").replace("{ARCH}","").replace("{EXT}", "zip")
            "Linux" -> DEFAULT_URL.replace("{OS}", "linux-glibc2.28").replace("{ARCH}", "-$arch").replace("{EXT}", "tar.xz")
            "MacOSX" -> DEFAULT_URL.replace("{OS}", "macos14").replace("{ARCH}", "-arm64").replace("{EXT}", "tar.gz")
            else -> throw IllegalStateException("Unsupported OS")
        }

        return DefaultCommandBuilder(name(), url)
            .setFileName("mysql")
            .setExtractCommand("tar -xvf")
            .addPostExtractCommands("echo export PATH=\$PATH:/usr/local/mysql/bin >> ~/.bashrc")
            .useSudo()
            .build()

    }
}