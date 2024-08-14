package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.VersionControlSystem
import com.tomassirio.easyinstaller.service.impl.strategy.StrategyFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@VersionControlSystem
class GitInstaller(
    private val shellFormatter: ShellFormatter,
    private val strategyFactory: StrategyFactory
) : InstallableApplication {

    @Value("\${command.default.git}")
    lateinit var DEFAULT_COMMAND: String

    override fun install(packageManager: String?) {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = strategyFactory.getStrategy(packageManager)
        val command = if (packageManager.isNullOrEmpty()) DEFAULT_COMMAND else name()
        strategy(command)
    }

    override fun name() = "Git"
}