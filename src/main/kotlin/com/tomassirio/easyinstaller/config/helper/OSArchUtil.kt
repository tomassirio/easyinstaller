package com.tomassirio.easyinstaller.config.helper

import org.springframework.stereotype.Component
import java.util.*

@Component
class OSArchUtil(
        private val systemPropertyWrapper: SystemPropertyWrapper = DefaultSystemPropertyWrapper(),
        private val fileChecker: FileChecker = DefaultFileChecker()
) {
    fun getOS(): String {
        val os = systemPropertyWrapper.getProperty("os.name")?.lowercase(Locale.ENGLISH) ?: ""
        return when {
            os.contains("mac") || os.contains("darwin") -> "MacOSX"
            os.contains("win") -> "Windows"
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> detectLinuxDistribution()
            else -> "default"
        }
    }

    fun getArch(): String {
        val arch = systemPropertyWrapper.getProperty("os.arch")?.lowercase(Locale.ENGLISH) ?: ""
        return when {
            arch.contains("amd64") || arch.contains("x86_64") -> "x86_64"
            arch.contains("aarch64") || arch.contains("arm64") -> "arm64"
            else -> "Unknown"
        }
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