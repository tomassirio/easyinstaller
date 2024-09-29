package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.PackageManager
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@PackageManager
@Profile("MacOSX")
class BrewInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
) : InstallableApplication {

   @Value("\${url.default.brew}")
   lateinit var DEFAULT_URL: String
    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Brew"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .executeAsScript("bash")
                .useSudo()
                .build()
    }
}