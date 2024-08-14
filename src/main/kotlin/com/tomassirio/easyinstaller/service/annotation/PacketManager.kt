package com.tomassirio.easyinstaller.service.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PacketManager(
    val enabled: Boolean = true
)