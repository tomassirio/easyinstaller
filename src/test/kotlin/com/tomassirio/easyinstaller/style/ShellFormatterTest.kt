package com.tomassirio.easyinstaller.style

import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ShellFormatterTest {

    @Mock
    lateinit var terminal: Terminal

    @InjectMocks
    lateinit var shellFormatter: ShellFormatter

    @BeforeEach
    fun setUp() {
        shellFormatter.infoColor = "CYAN"
        shellFormatter.successColor = "GREEN"
        shellFormatter.warningColor = "YELLOW"
        shellFormatter.errorColor = "RED"
    }

    @Test
    fun `test getColored`() {
        val message = "Test Message"
        val color = PromptColor.CYAN
        val expected = AttributedStringBuilder().append(
            message,
            AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())
        ).toAnsi()

        val result = shellFormatter.getColored(message, color)
        assertEquals(expected, result)
    }

    @Test
    fun `test print`() {
        val message = "Test Message"
        val writer = mock(java.io.PrintWriter::class.java)
        `when`(terminal.writer()).thenReturn(writer)

        shellFormatter.print(message)
        verify(writer).println(message)
        verify(terminal).flush()
    }

    @Test
    fun `test printSuccess`() {
        val message = "Success Message"
        val writer = mock(java.io.PrintWriter::class.java)
        `when`(terminal.writer()).thenReturn(writer)

        shellFormatter.printSuccess(message)
        verify(writer).println(shellFormatter.getColored(message, PromptColor.GREEN))
        verify(terminal).flush()
    }

    @Test
    fun `test printInfo`() {
        val message = "Info Message"
        val writer = mock(java.io.PrintWriter::class.java)
        `when`(terminal.writer()).thenReturn(writer)

        shellFormatter.printInfo(message)
        verify(writer).println(shellFormatter.getColored(message, PromptColor.CYAN))
        verify(terminal).flush()
    }

    @Test
    fun `test printWarning`() {
        val message = "Warning Message"
        val writer = mock(java.io.PrintWriter::class.java)
        `when`(terminal.writer()).thenReturn(writer)

        shellFormatter.printWarning(message)
        verify(writer).println(shellFormatter.getColored(message, PromptColor.YELLOW))
        verify(terminal).flush()
    }

    @Test
    fun `test printError`() {
        val message = "Error Message"
        val writer = mock(java.io.PrintWriter::class.java)
        `when`(terminal.writer()).thenReturn(writer)

        shellFormatter.printError(message)
        verify(writer).println(shellFormatter.getColored(message, PromptColor.RED))
        verify(terminal).flush()
    }
}