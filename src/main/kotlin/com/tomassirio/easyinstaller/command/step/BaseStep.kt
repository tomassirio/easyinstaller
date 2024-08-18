package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.InstallableApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

abstract class BaseStep(
    val applicationContext: ApplicationContext,
    val environment: Environment
) {
    fun <T : Annotation> getInstallers(annotationClass: Class<T>): List<InstallableApplication> {
        val activeProfiles = environment.activeProfiles.toSet()

        return applicationContext.getBeansWithAnnotation(annotationClass)
            .values
            .filterIsInstance<InstallableApplication>()
            .filter { app ->
                // Find the annotation using reflection
                val annotation = app::class.java.getAnnotation(annotationClass)

                // Assuming "enabled" is a property in your annotation
                val enabled = annotation?.let { ann ->
                    val enabledMethod = annotationClass.getMethod("enabled")
                    enabledMethod.invoke(ann) as? Boolean
                } ?: false

                // Check if the bean has a @Profile annotation
                val hasProfileAnnotation = app::class.java.getAnnotation(Profile::class.java) != null
                val profiles = if (hasProfileAnnotation) {
                    app::class.java.getAnnotation(Profile::class.java)?.value?.toSet() ?: emptySet()
                } else {
                    emptySet()
                }

                // Filter based on profiles
                enabled && (profiles.isEmpty() || profiles.any { it in activeProfiles })
            }
    }

}
