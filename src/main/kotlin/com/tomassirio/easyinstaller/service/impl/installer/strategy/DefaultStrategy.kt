package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.service.impl.installer.strategy.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Component

@Component
class DefaultStrategy(
        shellFormatter: ShellFormatter,
        processBuilderFactory: ProcessBuilderFactory,
) : BaseStrategy(shellFormatter, processBuilderFactory), DownloadStrategy {

    override fun install(urlOrName: String?) {
        urlOrName?.let(::process)
        ?: run { shellFormatter.printWarning("No default command found for this app")}
    }

    override fun name(): String {
        return "default"
    }
}