package com.tomassirio.easyinstaller.service.impl.installer.builder

import java.net.URL

class DefaultCommandBuilder(
        name: String,
        private val defaultUrl: String
) {
    private var curlCommand: String = "curl -fsSL"
    private var fileName: String? = null
    private var extractCommand: String? = null
    private var postExtractCommands: List<String> = emptyList()
    private var cleanupCommands: List<String> = emptyList()
    private var workingDirectory: String = "/tmp/installer-${name.lowercase()}"
    private var sudo: Boolean = false
    private var verifyChecksum: Boolean = false
    private var checksumUrl: String? = null
    private var checksumAlgorithm: String = "sha256sum"
    private var isScriptExecution: Boolean = false
    private var scriptInterpreter: String = "bash"

    fun setFileName(name: String): DefaultCommandBuilder {
        this.fileName = name
        return this
    }

    fun setExtractCommand(command: String): DefaultCommandBuilder {
        this.extractCommand = command
        return this
    }

    fun setCurlCommand(command: String): DefaultCommandBuilder {
        this.curlCommand = command
        return this
    }

    fun addPostExtractCommands(vararg commands: String): DefaultCommandBuilder {
        this.postExtractCommands = commands.toList()
        return this
    }

    fun addCleanupCommands(vararg commands: String): DefaultCommandBuilder {
        this.cleanupCommands = commands.toList()
        return this
    }

    fun setWorkingDirectory(directory: String): DefaultCommandBuilder {
        this.workingDirectory = directory
        return this
    }

    fun useSudo(use: Boolean = true): DefaultCommandBuilder {
        this.sudo = use
        return this
    }

    fun verifyChecksum(verify: Boolean = true, checksumUrl: String? = null, algorithm: String = "sha256sum"): DefaultCommandBuilder {
        this.verifyChecksum = verify
        this.checksumUrl = checksumUrl
        this.checksumAlgorithm = algorithm
        return this
    }

    fun executeAsScript(interpreter: String = "bash"): DefaultCommandBuilder {
        this.isScriptExecution = true
        this.scriptInterpreter = interpreter
        return this
    }

    fun build(): String {
        return if (isScriptExecution) {
            buildScriptExecution()
        } else {
            buildFileBasedInstallation()
        }
    }

    private fun buildScriptExecution(): String {
        return buildString {
            append("$curlCommand $defaultUrl | ")
            append(scriptInterpreter.withSudoIfNeeded())
        }
    }

    private fun buildFileBasedInstallation(): String {
        val actualFileName = fileName ?: guessFileName()
        val actualExtractCommand = extractCommand ?: guessExtractCommand(actualFileName)

        return buildString {
            append("mkdir -p $workingDirectory && ")
            append("cd $workingDirectory && ")
            append("$curlCommand $defaultUrl -o $actualFileName && ")

            if (verifyChecksum) {
                append(buildChecksumVerification())
            }

            append("$actualExtractCommand && ")
            postExtractCommands.forEach { append("${it.withSudoIfNeeded()} && ") }
            cleanupCommands.forEach { append("${it.withSudoIfNeeded()} && ") }
            append("cd - && ")
            append("rm -rf $workingDirectory")
        }
    }

    private fun guessFileName(): String {
        return URL(defaultUrl).path.split("/").last()
    }

    private fun guessExtractCommand(fileName: String): String {
        return when {
            fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz") -> "tar -xzvf $fileName"
            fileName.endsWith(".tar.bz2") -> "tar -xjvf $fileName"
            fileName.endsWith(".tar") -> "tar -xvf $fileName"
            fileName.endsWith(".zip") -> "unzip $fileName"
            fileName.endsWith(".deb") -> "sudo dpkg -i $fileName"
            fileName.endsWith(".rpm") -> "sudo rpm -i $fileName"
            else -> throw IllegalArgumentException("Unable to determine extract command for $fileName")
        }
    }

    private fun buildChecksumVerification(): String {
        val checksumFile = "${fileName}.checksum"
        return buildString {
            append("$curlCommand ${checksumUrl ?: "$defaultUrl.checksum"} -o $checksumFile && ")
            append("$checksumAlgorithm -c $checksumFile || (echo 'Checksum verification failed'; exit 1) && ")
        }
    }

    private fun String.withSudoIfNeeded(): String {
        return if (sudo && !this.startsWith("sudo")) "sudo $this" else this
    }
}