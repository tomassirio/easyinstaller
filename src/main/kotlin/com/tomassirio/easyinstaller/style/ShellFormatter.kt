package com.tomassirio.easyinstaller.style

import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ShellFormatter(val terminal: Terminal) {
    @Value("\${shell.out.info}")
    lateinit var infoColor: String

    @Value("\${shell.out.success}")
    lateinit var successColor: String

    @Value("\${shell.out.warning}")
    lateinit var warningColor: String

    @Value("\${shell.out.error}")
    lateinit var errorColor: String

    @Value("\${shell.out.output}")
    lateinit var outputColor: String

    fun getColored(message: String?, color: PromptColor): String {
        return AttributedStringBuilder().append(
            message,
            AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())
        ).toAnsi()
    }

    fun print(message: String?) {
        print(message, null)
    }

    fun printSuccess(message: String?) {
        print(message, PromptColor.valueOf(successColor))
    }

    fun printInfo(message: String?) {
        print(message, PromptColor.valueOf(infoColor))
    }

    fun printWarning(message: String?) {
        print(message, PromptColor.valueOf(warningColor))
    }

    fun printError(message: String?) {
        print(message, PromptColor.valueOf(errorColor))
    }

    fun printOutput(message: String?) {
        print(message, PromptColor.valueOf(outputColor))
    }

    fun print(message: String?, color: PromptColor?) {
        var toPrint = message
        if (color != null) {
            toPrint = getColored(message, color)
        }
        terminal.writer().println(toPrint)
        terminal.flush()
    }
}