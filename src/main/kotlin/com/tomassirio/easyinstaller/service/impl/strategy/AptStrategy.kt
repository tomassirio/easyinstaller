package com.tomassirio.easyinstaller.service.impl.strategy

import com.tomassirio.easyinstaller.service.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("debian", "ubuntu")
class AptStrategy(
    shellFormatter: ShellFormatter,
    processBuilderFactory: ProcessBuilderFactory
) : BaseStrategy(shellFormatter, processBuilderFactory), DownloadStrategy {

    override fun install(urlOrName: String) {
        super.process("apt install -y $urlOrName")
    }

    override fun name(): String {
        return "apt"
    }
}