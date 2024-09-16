package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.service.impl.installer.strategy.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DefaultStrategyTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter
    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var processBuilder: ProcessBuilder
    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var processBuilderFactory: ProcessBuilderFactory
    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var process: Process

    @InjectMocks
    private lateinit var defaultStrategy: DefaultStrategy

    @Captor
    private lateinit var commandCaptor: ArgumentCaptor<String>

    @BeforeEach
    fun setUp() {
        `when`(processBuilderFactory.create()).thenReturn(processBuilder)
        `when`(processBuilder.start()).thenReturn(process)
        `when`(process.inputStream).thenReturn("".byteInputStream())
        `when`(process.waitFor()).thenReturn(0)
    }

    @Test
    fun `install should call process with correct command after decoration`() {
        val urlOrName = "example"

        defaultStrategy.install(urlOrName)

        verify(shellFormatter).printInfo(commandCaptor.capture())
        assertEquals("With commands: $urlOrName...",
                commandCaptor.value)
    }

    @Test
    fun `name should return correct name`() {
        assertEquals("default", defaultStrategy.name())
    }
}