package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.BuildAndCiCdTool
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@BuildAndCiCdTool
class GradleInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext
) : InstallableApplication {

    @Value("\${url.default.gradle}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Gradle"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .setFileName("gradle.zip")
                .addPostExtractCommands("unzip gradle.zip",
                        "sudo mv gradle-8.5 /opt/gradle",
                        "echo export PATH=\$PATH:/opt/gradle/bin >> ~/.bashrc")
                .addCleanupCommands("rm gradle.zip")
                .useSudo()
                .build()
    }
}