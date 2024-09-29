package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
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
import java.util.Properties
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class CondaInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var downloadStrategyContext: DownloadStrategyContext

    @Mock
    private lateinit var osArchUtil: OSArchUtil

    @InjectMocks
    private lateinit var condaInstaller: CondaInstaller

    private val defaultCommandField = "DEFAULT_URL"

    @BeforeEach
    fun setUp() {
        val properties = Properties()
        properties.load(FileInputStream("src/main/resources/application.properties"))
        val defaultCommand = properties.getProperty("url.default.conda")

        ReflectionTestUtils.setField(condaInstaller, defaultCommandField, defaultCommand)
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        `when`(downloadStrategyContext.isDefault()).thenReturn(true)
        `when`(osArchUtil.getOS()).thenReturn("MacOSX")
        `when`(osArchUtil.getArch()).thenReturn("aarch64")

        condaInstaller.install()

        verify(shellFormatter).printInfo("Installing Conda...")
        val finalURL = condaInstaller.DEFAULT_URL
            .replace("{OS}", "MacOSX")
            .replace("{ARCH}", "aarch64")
        verify(strategy).invoke("curl -fsSL $finalURL | sudo bash")
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: DownloadStrategy = mock(BrewStrategy::class.java)

        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy::install)

        condaInstaller.install()

        verify(shellFormatter).printInfo("Installing Conda...")
        verify(strategy).install("conda")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())

        assertThrows<RuntimeException> {
            condaInstaller.install()
        }

        verify(shellFormatter).printInfo("Installing Conda...")
    }

    @ParameterizedTest
    @MethodSource("getPossibleOutcomes")
    fun `test default curl URL is valid`(os: String, arch: String) {
        var finalURL = condaInstaller.DEFAULT_URL
            .replace("{OS}", os)
            .replace("{ARCH}", arch)

        if (os == "Windows") {
            finalURL = finalURL.replace(".sh", ".exe")
        }

        // Retrieve the default command automatically
        val modifiedCommand = "curl --head $finalURL"
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
        assertTrue(output.contains("HTTP/2 200"), "Expected output to contain 'HTTP/2 200'. Output was: $output")
    }

    companion object {
        @JvmStatic
        private fun getPossibleOutcomes(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("MacOSX", "arm64"),
                Arguments.of("MacOSX", "x86_64"),
                Arguments.of("Windows", "x86_64"),
                Arguments.of("Linux", "x86_64"),
                Arguments.of("Linux", "aarch64"),
            )
        }
    }
}