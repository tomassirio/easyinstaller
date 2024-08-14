package com.tomassirio.easyinstaller.service.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ide(
    val enabled: Boolean = true
)
