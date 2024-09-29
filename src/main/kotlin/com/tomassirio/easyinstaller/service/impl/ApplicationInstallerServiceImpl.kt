package com.tomassirio.easyinstaller.service.impl

import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.service.InstallableApplication
import org.springframework.stereotype.Service

@Service
class ApplicationInstallerServiceImpl(
    private val applications: List<InstallableApplication>,
)  : ApplicationInstallerService {
    override fun installAllInOrder() {
        applications.forEach { app ->
            app.install()
        }
    }

    override fun listApplications() = applications.map { it.name() }

    override fun installApplication(name: String) {
        val app = applications.find { it.name() == name }
        if (app != null) {
            app.install()
        } else {
            throw IllegalArgumentException("Application $name not found")
        }
    }
}