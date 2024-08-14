package com.tomassirio.easyinstaller.service.process

interface ProcessBuilderFactory {
    fun create(): ProcessBuilder
}