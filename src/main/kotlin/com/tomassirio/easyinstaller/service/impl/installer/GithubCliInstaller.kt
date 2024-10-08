package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.VersionControlSystem
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@VersionControlSystem
class GithubCliInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
) : InstallableApplication {

    @Value("\${url.default.github_cli}")
    lateinit var DEFAULT_URL: String

    private final var alias = "gh"

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else alias
        strategy(command)
    }
    override fun name() = "Github Cli"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .executeAsScript("bash")
                .useSudo()
                .build()
    }
}