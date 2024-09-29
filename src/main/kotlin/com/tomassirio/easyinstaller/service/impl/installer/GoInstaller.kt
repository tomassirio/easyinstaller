package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.ProgrammingLanguageTool
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@ProgrammingLanguageTool
class GoInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext,
        private val osArchUtil: OSArchUtil
) : InstallableApplication {

    @Value("\${url.default.go}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Go"

    private fun createDefaultCommand(): String {
        val os = osArchUtil.getOS()
            .replace("MacOSX", "darwin")
            .replace("Linux", "linux")
            .replace("Windows", "windows")
        val arch = osArchUtil.getArch()
            .replace("aarch64", "arm64")

        val ext = if (os == "windows") "zip" else "tar.gz"

        if (os == "Unknown" || arch == "Unknown") {
            throw IllegalStateException("Unsupported OS or architecture")
        }

        val url = DEFAULT_URL.replace("{OS}", os).replace("{ARCH}", arch).replace("{EXT}", ext)

        return DefaultCommandBuilder(name(), url)
                .setFileName("go.$ext")
                .setExtractCommand("tar -C /usr/local -xz")
                .addPostExtractCommands("echo export PATH=\$PATH:/usr/local/go/bin >> ~/.bashrc")
                .addCleanupCommands("rm go.$ext")
                .useSudo()
                .build()
    }
}