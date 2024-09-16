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
class MavenInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
): InstallableApplication {

    @Value("\${url.default.maven}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Maven"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .setFileName("apache-maven.tar.gz")
                .setExtractCommand("tar -xz")
                .addPostExtractCommands(
                        "sudo mv apache-maven /opt/maven",
                        "echo export PATH=\$PATH:/opt/maven/bin >> ~/.bashrc"
                )
                .build()
    }
}