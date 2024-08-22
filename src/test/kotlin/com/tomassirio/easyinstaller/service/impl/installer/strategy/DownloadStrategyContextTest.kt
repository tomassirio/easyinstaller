package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.exception.StrategyNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class DownloadStrategyContextTest {

    private lateinit var defaultStrategy: DownloadStrategy
    private lateinit var customStrategy: DownloadStrategy
    private lateinit var downloadStrategyContext: DownloadStrategyContext

    @BeforeEach
    fun setUp() {
        defaultStrategy = mock(DefaultStrategy::class.java)
        customStrategy = mock(DownloadStrategy::class.java)

        `when`(defaultStrategy.name()).thenReturn("Default")
        `when`(customStrategy.name()).thenReturn("Custom")

        downloadStrategyContext = DownloadStrategyContext(listOf(defaultStrategy, customStrategy))
    }

    @Test
    fun `getCurrentStrategy should return the default strategy initially`() {
        val strategy = downloadStrategyContext.getCurrentStrategy()
        assertEquals(defaultStrategy::install, strategy)
    }

    @Test
    fun `isDefault should return true when the current strategy is the default strategy`() {
        assertTrue(downloadStrategyContext.isDefault())
    }

    @Test
    fun `setCurrentStrategyByName should set the strategy to the specified name`() {
        downloadStrategyContext.setCurrentStrategyByName("Custom")
        val strategy = downloadStrategyContext.getCurrentStrategy()
        assertEquals(customStrategy::install, strategy)
    }

    @Test
    fun `setCurrentStrategyByName should throw StrategyNotFoundException for an invalid name`() {
        assertThrows<StrategyNotFoundException> {
            downloadStrategyContext.setCurrentStrategyByName("Invalid")
        }
    }
}