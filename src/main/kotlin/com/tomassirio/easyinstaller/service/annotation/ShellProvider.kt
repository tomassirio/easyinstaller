package com.tomassirio.easyinstaller.service.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ShellProvider(
    val enabled: Boolean = true
)
