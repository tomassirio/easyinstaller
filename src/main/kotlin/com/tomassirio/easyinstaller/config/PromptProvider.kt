package com.tomassirio.easyinstaller.config

import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.jline.utils.AttributedString
import org.springframework.stereotype.Component

@Component
class PromptProvider: PromptProvider {
    override fun getPrompt(): AttributedString {
        return AttributedString(
            "EASY-INSTALLER:>",
            AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
                .blinkDefault()
                .bold()
        )
    }
}