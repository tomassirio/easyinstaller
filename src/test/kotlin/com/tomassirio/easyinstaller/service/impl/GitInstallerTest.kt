package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.impl.strategy.StrategyFactory
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
    private lateinit var strategyFactory: StrategyFactory

    @InjectMocks
    private lateinit var gitInstaller: GitInstaller

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(gitInstaller, "DEFAULT_COMMAND", "sudo apt-get install git")
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String) -> Unit = mock()
        `when`(strategyFactory.getStrategy(null)).thenReturn(strategy)

        gitInstaller.install(null)

        verify(shellFormatter).printInfo("Installing Git...")
        verify(strategy).invoke("sudo apt-get install git")
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: (String) -> Unit = mock()
        `when`(strategyFactory.getStrategy("custom")).thenReturn(strategy)

        gitInstaller.install("custom")

        verify(shellFormatter).printInfo("Installing Git...")
        verify(strategy).invoke("Git")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String) -> Unit = mock()
        `when`(strategyFactory.getStrategy(null)).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())

        assertThrows<RuntimeException> {
            gitInstaller.install(null)
        }

        verify(shellFormatter).printInfo("Installing Git...")
    }
}