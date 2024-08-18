package com.tomassirio.easyinstaller.service.impl.installer.strategy

interface DownloadStrategy {
    fun install(urlOrName: String)

    fun name(): String
}