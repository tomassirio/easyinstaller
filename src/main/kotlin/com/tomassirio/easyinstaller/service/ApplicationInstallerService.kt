package com.tomassirio.easyinstaller.service

interface ApplicationInstallerService {
    fun installAllInOrder()

    fun listApplications() : List<String>

    fun installApplication(name: String)
}
