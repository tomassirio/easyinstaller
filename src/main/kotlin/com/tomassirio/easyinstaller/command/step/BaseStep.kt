package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.InstallableApplication
import org.springframework.context.ApplicationContext
import kotlin.reflect.full.findAnnotation

abstract class BaseStep(
    val applicationContext: ApplicationContext
) {
    protected inline fun <reified T> getInstallers(): List<InstallableApplication> where T : Annotation {
        return applicationContext.getBeansWithAnnotation(T::class.java)
            .values
            .filterIsInstance<InstallableApplication>()
            .filter { app ->
                val annotation = app::class.findAnnotation<T>()
                val enabled = annotation?.let { ann -> ann::class.members.find { it.name == "enabled" }?.call(ann) as? Boolean } ?: false
                enabled
            }
    }
}