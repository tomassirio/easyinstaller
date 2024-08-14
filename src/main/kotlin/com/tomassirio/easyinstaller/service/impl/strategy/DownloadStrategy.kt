package com.tomassirio.easyinstaller.service.impl.strategy

interface DownloadStrategy {
    fun install(urlOrName: String)
}