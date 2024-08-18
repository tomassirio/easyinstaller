package com.tomassirio.easyinstaller.service

import org.springframework.stereotype.Service

@Service
class ApplicationInstallerService(
    private val applications: List<InstallableApplication>,
) {
    fun installAllInOrder() {
        applications.forEach { app ->
            app.install()
        }
    }

    fun listApplications() = applications.map { it.name() }

    fun installApplication(name: String) {
        val app = applications.find { it.name() == name }
        if (app != null) {
            app.install()
        } else {
            throw IllegalArgumentException("Application $name not found")
        }
    }
}