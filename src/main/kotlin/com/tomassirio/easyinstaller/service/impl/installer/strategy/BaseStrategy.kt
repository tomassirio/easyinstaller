package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.service.impl.installer.strategy.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class BaseStrategy(
    val shellFormatter: ShellFormatter,
    private val processBuilderFactory: ProcessBuilderFactory
) {

    fun process(command: String) {
        shellFormatter.printInfo("With commands: $command...")

        runCatching {
            val process = createProcess(command)
            val output = readProcessOutput(process)
            printOutput(output)
            handleExitCode(process.waitFor())
        }.onFailure { error ->
            shellFormatter.printError("An error occurred while executing the command: ${error.message}")
        }
    }

    private fun createProcess(command: String): Process {
        return processBuilderFactory.create()
            .command("/bin/bash", "-c", command)
            .redirectErrorStream(true)
            .start()
    }

    private fun readProcessOutput(process: Process): List<String> {
        return BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.lineSequence().toList()
        }
    }

    private fun printOutput(output: List<String>) {
        output.forEach { line -> shellFormatter.printOutput(line) }
    }

    private fun handleExitCode(exitCode: Int) {
        if (exitCode == 0) {
            shellFormatter.printSuccess("Installation completed successfully")
        } else {
            shellFormatter.printError("Installation failed. Exit code: $exitCode")
        }
    }
}