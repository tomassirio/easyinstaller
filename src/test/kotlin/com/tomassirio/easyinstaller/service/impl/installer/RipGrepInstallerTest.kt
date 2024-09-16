package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.impl.installer.strategy.BrewStrategy
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategy
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class RipGrepInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var downloadStrategyContext: DownloadStrategyContext

    @InjectMocks
    private lateinit var ripGrepInstaller: RipGrepInstaller

    private val defaultCommandField = "DEFAULT_URL"

    @BeforeEach
    fun setUp() {
        val properties = Properties()
        properties.load(FileInputStream("src/main/resources/application.properties"))
        val defaultCommand = properties.getProperty("url.default.ripgrep")

        ReflectionTestUtils.setField(ripGrepInstaller, defaultCommandField, defaultCommand)
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        `when`(downloadStrategyContext.isDefault()).thenReturn(true)

        ripGrepInstaller.install()

        verify(shellFormatter).printInfo("Installing RipGrep...")
        verify(strategy).invoke(matches(
                "mkdir -p /tmp/installer-[a-f0-9-]+ && " +
                        "cd /tmp/installer-[a-f0-9-]+ && " +
                        "curl -fsSL https://github\\.com/BurntSushi/ripgrep/releases/download/14\\.0\\.3/ripgrep-14\\.0\\.3-x86_64-unknown-linux-musl\\.tar\\.gz -o ripgrep-14\\.0\\.3-x86_64-unknown-linux-musl\\.tar\\.gz && " +
                        "tar -xz && " +
                        "sudo cd ripgrep-14\\.0\\.3-x86_64-unknown-linux-musl && " +
                        "sudo cp rg /usr/local/bin/ && " +
                        "sudo rm -rf ripgrep-14\\.0\\.3-x86_64-unknown-linux-musl && " +
                        "cd - && " +
                        "rm -rf /tmp/installer-[a-f0-9-]+"
        ))
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: DownloadStrategy = mock(BrewStrategy::class.java)

        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy::install)

        ripGrepInstaller.install()

        verify(shellFormatter).printInfo("Installing RipGrep...")
        verify(strategy).install("ripgrep")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())

        assertThrows<RuntimeException> {
            ripGrepInstaller.install()
        }

        verify(shellFormatter).printInfo("Installing RipGrep...")
    }

    @Test
    fun `test default curl URL is valid`() {
        // Retrieve the default command automatically
        val modifiedCommand = "curl --head ${ripGrepInstaller.DEFAULT_URL}"
        val command = arrayOf("/bin/bash", "-c", modifiedCommand)

        // Start the process
        val process = ProcessBuilder(*command).start()

        // Add a timeout to avoid hanging indefinitely
        val exitCode = if (process.waitFor(3, TimeUnit.SECONDS)) {
            process.exitValue()
        } else {
            process.destroy()
            throw RuntimeException("Process timed out")
        }

        // Capture both output and error streams
        val output = process.inputStream.bufferedReader().readText()
        val errorOutput = process.errorStream.bufferedReader().readText()

        // Assert the process exited successfully and produced expected output
        assertEquals(0, exitCode, "Process failed with exit code $exitCode and error: $errorOutput")
        assertTrue(output.contains("HTTP/1.1 200 OK")
                .or(output.contains("HTTP/2 302")), "Expected output to contain 'HTTP/1.1 200 OK'. Output was: $output")
    }
}