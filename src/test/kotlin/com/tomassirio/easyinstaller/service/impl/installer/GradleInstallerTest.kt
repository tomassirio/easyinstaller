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
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class GradleInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var downloadStrategyContext: DownloadStrategyContext

    @InjectMocks
    private lateinit var gradleInstaller: GradleInstaller

    private val defaultCommandField = "DEFAULT_URL"

    @BeforeEach
    fun setUp() {
        val properties = Properties()
        properties.load(FileInputStream("src/main/resources/application.properties"))
        val defaultCommand = properties.getProperty("url.default.gradle")

        ReflectionTestUtils.setField(gradleInstaller, defaultCommandField, defaultCommand)
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        `when`(downloadStrategyContext.isDefault()).thenReturn(true)

        gradleInstaller.install()

        verify(shellFormatter).printInfo("Installing Gradle...")
        verify(strategy).invoke(    "mkdir -p /tmp/installer-gradle && " +
                "cd /tmp/installer-gradle && " +
                "curl -fsSL ${gradleInstaller.DEFAULT_URL} -o gradle.zip && " +
                "unzip gradle.zip && " +
                "sudo unzip gradle.zip && " +
                "sudo mv gradle-8.5 /opt/gradle && " +
                "sudo echo export PATH=\$PATH:/opt/gradle/bin >> ~/.bashrc && " +
                "sudo rm gradle.zip && " +
                "cd - && " +
                "rm -rf /tmp/installer-gradle")
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: DownloadStrategy = mock(BrewStrategy::class.java)

        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy::install)

        gradleInstaller.install()

        verify(shellFormatter).printInfo("Installing Gradle...")
        verify(strategy).install("gradle")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())

        assertThrows<RuntimeException> {
            gradleInstaller.install()
        }

        verify(shellFormatter).printInfo("Installing Gradle...")
    }

    @Test
    fun `test default curl URL is valid`() {
        // Retrieve the default command automatically
        val modifiedCommand = "curl --head ${gradleInstaller.DEFAULT_URL}"
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
                .or(output.contains("HTTP/1.1 301")), "Expected output to contain 'HTTP/1.1 200 OK'. Output was: $output")
    }
}