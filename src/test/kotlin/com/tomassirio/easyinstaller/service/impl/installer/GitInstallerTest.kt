package com.tomassirio.easyinstaller.service.impl.installer

import com.tomassirio.easyinstaller.service.impl.installer.strategy.BrewStrategy
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategy
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class GitInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var downloadStrategyContext: DownloadStrategyContext

    @InjectMocks
    private lateinit var gitInstaller: GitInstaller

    private val DEFAULT_COMMAND = "DEFAULT_COMMAND"

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(gitInstaller, DEFAULT_COMMAND, DEFAULT_COMMAND)
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        `when`(downloadStrategyContext.isDefault()).thenReturn(true)

        gitInstaller.install()

        verify(shellFormatter).printInfo("Installing Git...")
        verify(strategy).invoke(DEFAULT_COMMAND)
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: DownloadStrategy = mock(BrewStrategy::class.java)

        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy::install)
        gitInstaller.install()

        verify(shellFormatter).printInfo("Installing Git...")
        verify(strategy).install("git")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String) -> Unit = mock()
        `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())


        assertThrows<RuntimeException> {
            gitInstaller.install()
        }

        verify(shellFormatter).printInfo("Installing Git...")
    }
}