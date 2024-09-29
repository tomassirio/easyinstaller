package com.tomassirio.easyinstaller.service.impl.installer.strategy.process

interface ProcessBuilderFactory {
    fun create(): ProcessBuilder
}