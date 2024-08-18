package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.service.impl.installer.strategy.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class BaseStrategyTest {

    private lateinit var shellFormatter: ShellFormatter
    private lateinit var processBuilderFactory: ProcessBuilderFactory
    private lateinit var processBuilder: ProcessBuilder
    private lateinit var process: Process
    private lateinit var baseStrategy: BaseStrategy

    @Captor
    private lateinit var outputCaptor: ArgumentCaptor<String>


    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        shellFormatter = mock(ShellFormatter::class.java)
        processBuilderFactory = mock(ProcessBuilderFactory::class.java)
        processBuilder = mock(ProcessBuilder::class.java)
        process = mock(Process::class.java)

        baseStrategy = object : BaseStrategy(shellFormatter, processBuilderFactory) {}

        `when`(processBuilderFactory.create()).thenReturn(processBuilder)
        `when`(processBuilder.command(anyList<String>())).thenReturn(processBuilder)
        `when`(processBuilder.redirectErrorStream(anyBoolean())).thenReturn(processBuilder)
        `when`(processBuilder.start()).thenReturn(process)
    }

    @Test
    fun `test process successful execution`() {
        val command = "echo 'Hello World'"
        val outputLine = "Hello World"

        // Mock process input stream
        val inputStream = ByteArrayInputStream("$outputLine\n".toByteArray(StandardCharsets.UTF_8))
        `when`(process.inputStream).thenReturn(inputStream)

        // Mock process exit code
        `when`(process.waitFor()).thenReturn(0)

        // Ensure that processBuilder.command() returns the mock processBuilder
        `when`(processBuilder.command("/bin/bash", "-c", command)).thenReturn(processBuilder)

        // Execute the process method
        baseStrategy.process(command)
        verify(shellFormatter).printOutput(outputCaptor.capture())
        assertEquals(outputLine, outputCaptor.value)

        // Verify interactions
        verify(shellFormatter).printInfo("With commands: $command...")
        verify(shellFormatter).printOutput(outputLine)
        verify(shellFormatter).printSuccess("Installation completed successfully")
        verifyNoMoreInteractions(shellFormatter)
    }

    @Test
    fun `test process execution failure`() {
        val command = "exit 1"
        val errorLine = "An error occurred"

        // Mock process input stream
        val inputStream = ByteArrayInputStream("$errorLine\n".toByteArray(StandardCharsets.UTF_8))
        `when`(process.inputStream).thenReturn(inputStream)

        // Mock process exit code
        `when`(process.waitFor()).thenReturn(1)

        // Ensure that processBuilder.command() returns the mock processBuilder
        `when`(processBuilder.command("/bin/bash", "-c", command)).thenReturn(processBuilder)

        // Execute the process method
        baseStrategy.process(command)
        verify(shellFormatter).printOutput(outputCaptor.capture())
        assertEquals(errorLine, outputCaptor.value)

        // Verify interactions
        val inOrder = inOrder(shellFormatter)
        inOrder.verify(shellFormatter).printInfo("With commands: $command...")
        inOrder.verify(shellFormatter).printOutput(errorLine)
        inOrder.verify(shellFormatter).printError("Installation failed. Exit code: 1")
        verifyNoMoreInteractions(shellFormatter)
    }

    @Test
    fun `test process command throws exception`() {
        val command = "invalid_command"
        val exceptionMessage = "Command execution failed"

        // Ensure that processBuilder.command() returns the mock processBuilder
        `when`(processBuilder.command("/bin/bash", "-c", command)).thenReturn(processBuilder)

        // Mock the processBuilder.start() method to throw an exception
        `when`(processBuilder.start()).thenThrow(RuntimeException(exceptionMessage))

        // Execute the process method
        baseStrategy.process(command)

        // Verify interactions
        val inOrder = inOrder(shellFormatter)
        inOrder.verify(shellFormatter).printInfo("With commands: $command...")
        inOrder.verify(shellFormatter).printError(outputCaptor.capture())
        verifyNoMoreInteractions(shellFormatter)

        // Assert on the captured error message
        val capturedErrorMessage = outputCaptor.value
        assertTrue(capturedErrorMessage.startsWith("An error occurred while executing the command:"))
        assertTrue(capturedErrorMessage.endsWith(exceptionMessage))
    }

    @Test
    fun `test read process output`() {
        val outputLine1 = "Line 1"
        val outputLine2 = "Line 2"

        `when`(process.inputStream).thenReturn(
            ByteArrayInputStream((outputLine1 + "\n" + outputLine2).toByteArray(StandardCharsets.UTF_8))
        )

        val method = BaseStrategy::class.java.getDeclaredMethod("readProcessOutput", Process::class.java)
        method.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val output = method.invoke(baseStrategy, process) as List<String>

        assertEquals(listOf(outputLine1, outputLine2), output)
    }
}
