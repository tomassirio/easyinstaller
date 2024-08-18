package com.tomassirio.easyinstaller.config

import com.tomassirio.easyinstaller.config.helper.DefaultFileChecker
import com.tomassirio.easyinstaller.config.helper.DefaultSystemPropertyWrapper
import com.tomassirio.easyinstaller.config.helper.FileChecker
import com.tomassirio.easyinstaller.config.helper.SystemPropertyWrapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import java.util.*

class OsBasedProfileActivator(
    private val systemPropertyWrapper: SystemPropertyWrapper = DefaultSystemPropertyWrapper(),
    private val fileChecker: FileChecker = DefaultFileChecker()
) : EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val os = systemPropertyWrapper.getProperty("os.name")?.lowercase(Locale.getDefault()) ?: ""
        val activeProfile = when {
            os.contains("win") -> "windows"
            os.contains("mac") -> "mac"
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> detectLinuxDistribution()
            else -> "default"
        }

        environment.addActiveProfile(activeProfile)
    }

    private fun detectLinuxDistribution(): String {
        return when {
            isDebianBased() -> "debian"
            isFedoraBased() -> "fedora"
            isArchBased() -> "arch"
            else -> "linux"
        }
    }

    private fun isDebianBased(): Boolean {
        return fileChecker.fileExists("/etc/debian_version")
    }

    private fun isFedoraBased(): Boolean {
        return fileChecker.fileExists("/etc/fedora-release")
    }

    private fun isArchBased(): Boolean {
        return fileChecker.fileExists("/etc/arch-release")
    }
}

