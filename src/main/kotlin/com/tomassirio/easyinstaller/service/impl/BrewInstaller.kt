package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.annotation.PackageManager
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.impl.strategy.StrategyFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@PackageManager
class BrewInstaller(
    private val shellFormatter: ShellFormatter,
    private val strategyFactory: StrategyFactory
) : InstallableApplication {

   @Value("\${command.default.brew}")
   lateinit var DEFAULT_COMMAND: String
    override fun install(packageManager: String?) {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = strategyFactory.getStrategy(packageManager)
        val command = if (packageManager.isNullOrEmpty()) DEFAULT_COMMAND else name().lowercase()
        strategy(command)
    }

    override fun name() = "Brew"
}