package com.tomassirio.easyinstaller.config

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class PromptProvider: PromptProvider {
    override fun getPrompt(): AttributedString {
        // Customize the style for different parts of the prompt
        val titleStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
        val separatorStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)
        val promptStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).bold()

        // Define the parts of the prompt
        val title = AttributedString("EASY INSTALLER", titleStyle)
        val separator = AttributedString(" :: ", separatorStyle)
        val prompt = AttributedString(">", promptStyle)

        // Assemble the final prompt
        return AttributedString.join(AttributedString.EMPTY, title, separator, prompt)
    }
}