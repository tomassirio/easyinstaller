package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.IdesAndTextEditor
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@IdesAndTextEditor
class AtomInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext
): InstallableApplication {

    @Value("\${url.default.atom}")
    lateinit var DEFAULT_URL: String

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) DEFAULT_URL else name().lowercase()
        strategy(command)
    }

    override fun name() = "Atom"

    private fun createDefaultCommand(): String {
        return DefaultCommandBuilder(name(), DEFAULT_URL)
                .setFileName("apt_1.9.3_amd64.deb")
                .addPostExtractCommands(
                        "cp /usr/bin/apt /usr/bin/apt.backup",
                        "dpkg -i apt_1.9.3_amd64.deb",
                        "apt-get install -f"
                )
                .addCleanupCommands(
                        "rm apt_1.9.3_amd64.deb"
                )
                .useSudo()
                .build()
    }
}