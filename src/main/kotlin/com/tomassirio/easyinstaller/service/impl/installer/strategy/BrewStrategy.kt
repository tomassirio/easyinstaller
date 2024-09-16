package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.service.impl.installer.strategy.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("mac")
class BrewStrategy(
    shellFormatter: ShellFormatter,
    processBuilderFactory: ProcessBuilderFactory
) : BaseStrategy(shellFormatter, processBuilderFactory), DownloadStrategy {

    override fun install(urlOrName: String?) {
        super.process("brew install $urlOrName")
    }

    override fun name(): String {
        return "brew"
    }
}