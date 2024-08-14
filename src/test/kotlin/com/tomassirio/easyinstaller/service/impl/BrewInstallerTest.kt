package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayInputStream

private const val BREW_INSTALL_COMMAND = "sudo curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | /bin/bash"
private const val BASH_DIR = "/bin/bash"
private const val ARGUMENTS = "-c"

@ExtendWith(MockitoExtension::class)
class BrewInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var processBuilderFactory: ProcessBuilderFactory

    @InjectMocks
    private lateinit var brewInstaller: BrewInstaller

    @Test
    fun `test successful installation`() {
        val processBuilder: ProcessBuilder = mock()
        val process: Process = mock()
        val output = listOf("Installing Homebrew...", "Installation successful!")

        `when`(processBuilderFactory.create()).thenReturn(processBuilder)
        `when`(processBuilder.command(BASH_DIR, ARGUMENTS, BREW_INSTALL_COMMAND)).thenReturn(processBuilder)
        `when`(processBuilder.redirectErrorStream(anyBoolean())).thenReturn(processBuilder)
        `when`(processBuilder.start()).thenReturn(process)
        `when`(process.inputStream).thenReturn(ByteArrayInputStream(output.joinToString("\n").toByteArray()))
        `when`(process.waitFor()).thenReturn(0)

        brewInstaller.install()

        verify(shellFormatter).printInfo("Installing Brew...")
        output.forEach { verify(shellFormatter).print(it) }
        verify(shellFormatter).printSuccess("Brew installed successfully")
    }

    @Test
    fun `test failed installation`() {
        val processBuilder: ProcessBuilder = mock()
        val process: Process = mock()
        val output = listOf("Installing Homebrew...", "Installation failed!")

        `when`(processBuilderFactory.create()).thenReturn(processBuilder)
        `when`(processBuilder.command(BASH_DIR, ARGUMENTS, BREW_INSTALL_COMMAND)).thenReturn(processBuilder)
        `when`(processBuilder.redirectErrorStream(anyBoolean())).thenReturn(processBuilder)
        `when`(processBuilder.start()).thenReturn(process)
        `when`(process.inputStream).thenReturn(ByteArrayInputStream(output.joinToString("\n").toByteArray()))
        `when`(process.waitFor()).thenReturn(127)

        brewInstaller.install()

        verify(shellFormatter).printInfo("Installing Brew...")
        output.forEach { verify(shellFormatter).print(it) }
        verify(shellFormatter).printError("Failed to install Brew. Exit code: 127")
    }

    @Test
    fun `test exception handling`() {
        val processBuilder: ProcessBuilder = mock()

        `when`(processBuilderFactory.create()).thenReturn(processBuilder)
        `when`(processBuilder.command(BASH_DIR, ARGUMENTS, BREW_INSTALL_COMMAND)).thenReturn(processBuilder)
        `when`(processBuilder.redirectErrorStream(anyBoolean())).thenReturn(processBuilder)
        `when`(processBuilder.start()).thenThrow(RuntimeException("Test exception"))

        brewInstaller.install()

        verify(shellFormatter).printInfo("Installing Brew...")
        verify(shellFormatter).printError("An error occurred while installing Brew: Test exception")
    }
}