package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.ContainerAndVirtualizationTool
import com.tomassirio.easyinstaller.service.impl.installer.builder.DefaultCommandBuilder
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@ContainerAndVirtualizationTool
class DockerComposeInstaller(
    private val shellFormatter: ShellFormatter,
    private val downloadStrategyContext: DownloadStrategyContext,
    private val oSArchUtil: OSArchUtil
) : InstallableApplication {

    @Value("\${url.default.docker_compose}")
    lateinit var DEFAULT_URL: String

    private final val alias = "docker-compose"

    override fun install() {
        shellFormatter.printInfo("Installing ${name()}...")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        val command = if (downloadStrategyContext.isDefault()) createDefaultCommand() else alias
        strategy(command)
    }

    override fun name() = "Docker Compose"

    private fun createDefaultCommand(): String {
        val os = oSArchUtil.getOS()
            .replace("MacOSX", "darwin")
            .replace("Linux", "linux")
            .replace("Windows", "windows")
        val arch = oSArchUtil.getArch()

        var finalURL = DEFAULT_URL.replace("{OS}", os).replace("{ARCH}", arch)
        if (os == "windows") {
            finalURL = finalURL.plus(".exe")
        }

        return DefaultCommandBuilder(name(), finalURL)
            .setFileName("docker-compose")
            .addPostExtractCommands(
                "sudo mkdir -p /usr/local/lib/docker/cli-plugins",
                "sudo mv docker-compose /usr/local/lib/docker/cli-plugins",
                "sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose"
            )
            .useSudo()
            .build()
    }
}