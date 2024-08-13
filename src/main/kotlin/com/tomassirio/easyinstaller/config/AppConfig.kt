package com.tomassirio.easyinstaller.config

import com.tomassirio.easyinstaller.style.ShellFormatter
import org.jline.terminal.Terminal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class AppConfig(@Lazy private val terminal: Terminal) {
    @Bean
    fun shellFormatter(): ShellFormatter {
        return ShellFormatter(terminal)
    }
}