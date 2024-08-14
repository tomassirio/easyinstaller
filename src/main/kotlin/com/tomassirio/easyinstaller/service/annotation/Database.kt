package com.tomassirio.easyinstaller.service.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Database(
    val enabled: Boolean = true
)