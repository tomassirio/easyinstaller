package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
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
class CondaInstaller(
        private val shellFormatter: ShellFormatter,
        private val downloadStrategyContext: DownloadStrategyContext,
        private val osArchUtil: OSArchUtil
) : InstallableApplication {

    @Value("\${url.default.conda}")
    lateinit var DEFAULT_URL: String
    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else name().lowercase()
        strategy(command)
    }

    override fun name() = "Conda"

    private fun createDefaultCommand(): String {
        val os = osArchUtil.getOS()
        val arch = osArchUtil.getArch()

        if (os == "Unknown" || arch == "Unknown") {
            throw IllegalStateException("Unsupported OS or architecture")
        }

        var url = DEFAULT_URL.replace("{OS}", os).replace("{ARCH}", arch)
        if (os == "Windows") {
            url = url.replace(".sh", ".exe")
        }

        return DefaultCommandBuilder(name(), url)
                .executeAsScript("bash")
                .useSudo()
                .build()
    }
}