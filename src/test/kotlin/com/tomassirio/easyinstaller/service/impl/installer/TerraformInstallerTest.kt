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
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class TerraformInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var downloadStrategyContext: DownloadStrategyContext

    @Mock
    private lateinit var osArchUtil: OSArchUtil

    @InjectMocks
    private lateinit var terraformInstaller: TerraformInstaller

    private val defaultCommandField = "DEFAULT_URL"

    @BeforeEach
    fun setUp() {
        val properties = Properties()
        properties.load(FileInputStream("src/main/resources/application.properties"))
        val defaultCommand = properties.getProperty("url.default.terraform")

        ReflectionTestUtils.setField(terraformInstaller, defaultCommandField, defaultCommand)
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        `when`(downloadStrategyContext.isDefault()).thenReturn(true)
        `when`(osArchUtil.getOS()).thenReturn("MacOSX")
        `when`(osArchUtil.getArch()).thenReturn("arm64")

        terraformInstaller.install()

        verify(shellFormatter).printInfo("Installing Terraform...")
        verify(strategy).invoke(    "mkdir -p /tmp/installer-terraform && " +
                "cd /tmp/installer-terraform && " +
                "curl -fsSL ${terraformInstaller.DEFAULT_URL.replace("{OS}", "darwin").replace("{ARCH}", "arm64")} -o terraform.zip && " +
                "unzip && " +
                "sudo mv terraform /usr/local/bin/ && " +
                "sudo rm terraform.zip && " +
                "cd - && " +
                "rm -rf /tmp/installer-terraform")
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: DownloadStrategy = mock(BrewStrategy::class.java)

        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy::install)

        terraformInstaller.install()

        verify(shellFormatter).printInfo("Installing Terraform...")
        verify(strategy).install("terraform")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String?) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())

        assertThrows<RuntimeException> {
            terraformInstaller.install()
        }

        verify(shellFormatter).printInfo("Installing Terraform...")
    }

    @ParameterizedTest
    @MethodSource("osArchProvider")
    fun `test default curl URL is valid`(os: String, arch: String) {
        // Retrieve the default command automatically
        val modifiedCommand = "curl --head ${terraformInstaller.DEFAULT_URL.replace("{OS}", os).replace("{ARCH}", arch)}"
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
        assertTrue(output.contains("HTTP/2 200")
                .or(output.contains("HTTP/2 302")), "Expected output to contain 'HTTP/1.1 200 OK'. Output was: $output")
    }

    companion object {
        @JvmStatic
        fun osArchProvider() = listOf(
                arrayOf("darwin", "amd64"),
                arrayOf("darwin", "arm64"),
                arrayOf("linux", "arm64"),
                arrayOf("linux", "amd64"),
                arrayOf("windows", "amd64")
        )
    }
}