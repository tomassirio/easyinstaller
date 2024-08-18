package com.tomassirio.easyinstaller.service.impl.installer.strategy.decorator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SudoDecoratorTest {

    private lateinit var sudoDecorator: SudoDecorator

    @BeforeEach
    fun setUp() {
        sudoDecorator = SudoDecorator()
    }

    @Test
    fun `decorate should prepend sudo when sudoUsed is false`() {
        val command = "apt-get update"
        val expected = "sudo apt-get update"
        val result = sudoDecorator.decorate(command)
        assertEquals(expected, result)
    }

    @Test
    fun `decorate should not prepend sudo when sudoUsed is true`() {
        val command = "apt-get update"
        sudoDecorator.decorate(command)
        val result = sudoDecorator.decorate(command)
        assertEquals(command, result)
    }
}