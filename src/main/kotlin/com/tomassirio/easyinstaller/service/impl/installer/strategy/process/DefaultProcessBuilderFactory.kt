package com.tomassirio.easyinstaller.service.impl.installer.strategy.process

import org.springframework.stereotype.Component

@Component
class DefaultProcessBuilderFactory : ProcessBuilderFactory {
    override fun create(): ProcessBuilder = ProcessBuilder()
}