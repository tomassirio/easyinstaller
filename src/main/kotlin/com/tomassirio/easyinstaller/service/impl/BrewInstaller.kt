package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.annotation.PacketManager
import com.tomassirio.easyinstaller.service.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
@PacketManager
class BrewInstaller(
    private val shellFormatter: ShellFormatter,
    private val processBuilderFactory: ProcessBuilderFactory
) : InstallableApplication {

    companion object {
        const val BREW_INSTALL_COMMAND = "sudo curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | /bin/bash"
    }

    override fun install() {
        shellFormatter.printInfo("Installing Brew...")

        try {
            val process = createProcess()
            val output = readProcessOutput(process)
            printOutput(output)
            handleExitCode(process.waitFor())
        } catch (e: Exception) {
            shellFormatter.printError("An error occurred while installing Brew: ${e.message}")
        }
    }

    private fun createProcess(): Process {
        return processBuilderFactory.create()
            .command("/bin/bash", "-c", BREW_INSTALL_COMMAND)
            .redirectErrorStream(true)
            .start()
    }

    private fun readProcessOutput(process: Process): List<String> {
        return BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.lineSequence().toList()
        }
    }

    private fun printOutput(output: List<String>) {
        output.forEach { line -> shellFormatter.print(line) }
    }

    private fun handleExitCode(exitCode: Int) {
        if (exitCode == 0) {
            shellFormatter.printSuccess("Brew installed successfully")
        } else {
            shellFormatter.printError("Failed to install Brew. Exit code: $exitCode")
        }
    }

    override fun name() = "Brew"
}