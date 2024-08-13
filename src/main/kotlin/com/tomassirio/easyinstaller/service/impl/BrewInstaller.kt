package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.annotation.PacketManager
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
@PacketManager
class BrewInstaller(private val shellFormatter: ShellFormatter) : InstallableApplication {
    override fun install() {
        shellFormatter.printInfo("Installing Brew...")

        try {
            val process = ProcessBuilder("/bin/bash", "-c", "sudo curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | /bin/bash")
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                shellFormatter.print(line)
            }

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                shellFormatter.printSuccess("Brew installed successfully")
            } else {
                shellFormatter.printError("Failed to install Brew. Exit code: $exitCode")
            }
        } catch (e: Exception) {
            shellFormatter.printError("An error occurred while installing Brew: ${e.message}")
        }
    }

    override fun name() = "Brew"
}