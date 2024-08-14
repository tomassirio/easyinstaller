package com.tomassirio.easyinstaller.service.impl.strategy.decorator

import org.springframework.stereotype.Component

@Component
class SudoDecorator {
    private var sudoUsed = false

    fun decorate(command: String): String = when {
        sudoUsed -> command
        else -> "sudo $command".also { sudoUsed = true }
    }
}