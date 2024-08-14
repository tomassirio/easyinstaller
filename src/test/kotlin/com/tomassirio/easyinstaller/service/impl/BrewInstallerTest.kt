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
class BrewInstallerTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock
    private lateinit var strategyFactory: StrategyFactory

    @InjectMocks
    private lateinit var brewInstaller: BrewInstaller

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(brewInstaller, "DEFAULT_COMMAND", "sudo curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | /bin/bash")
    }

    @Test
    fun `test successful installation with default command`() {
        val strategy: (String) -> Unit = mock()
        `when`(strategyFactory.getStrategy(null)).thenReturn(strategy)

        brewInstaller.install(null)

        verify(shellFormatter).printInfo("Installing Brew...")
        verify(strategy).invoke("sudo curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | /bin/bash")
    }

    @Test
    fun `test successful installation with provided package manager`() {
        val strategy: (String) -> Unit = mock()
        `when`(strategyFactory.getStrategy("custom")).thenReturn(strategy)

        brewInstaller.install("custom")

        verify(shellFormatter).printInfo("Installing Brew...")
        verify(strategy).invoke("brew")
    }

    @Test
    fun `test exception handling during installation`() {
        val strategy: (String) -> Unit = mock()
        `when`(strategyFactory.getStrategy(null)).thenReturn(strategy)
        doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())

        assertThrows<RuntimeException> {
            brewInstaller.install(null)
        }

        verify(shellFormatter).printInfo("Installing Brew...")
    }
}