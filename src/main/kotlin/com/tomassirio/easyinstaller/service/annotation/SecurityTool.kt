package com.tomassirio.easyinstaller.service.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SecurityTool(
    val enabled: Boolean = true
)
