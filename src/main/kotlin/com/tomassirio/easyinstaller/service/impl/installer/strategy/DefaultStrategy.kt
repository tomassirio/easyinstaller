package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.service.impl.installer.strategy.decorator.SudoDecorator
import com.tomassirio.easyinstaller.service.impl.installer.strategy.process.ProcessBuilderFactory
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.springframework.stereotype.Component

@Component
class DefaultStrategy(
    shellFormatter: ShellFormatter,
    processBuilderFactory: ProcessBuilderFactory,
    private val sudoDecorator: SudoDecorator
) : BaseStrategy(shellFormatter, processBuilderFactory), DownloadStrategy {

    override fun install(urlOrName: String) {
        sudoDecorator.decorate(urlOrName)
            .let(::process)
    }

    override fun name(): String {
        return "default"
    }
}