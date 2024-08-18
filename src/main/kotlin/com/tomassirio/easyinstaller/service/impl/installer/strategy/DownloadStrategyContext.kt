package com.tomassirio.easyinstaller.service.impl.installer.strategy

import com.tomassirio.easyinstaller.exception.StrategyNotFoundException
import org.springframework.stereotype.Component

@Component
class DownloadStrategyContext(
    private val strategies: List<DownloadStrategy>
) {
    private var currentStrategy: DownloadStrategy = strategies.first { it is DefaultStrategy }

    fun getCurrentStrategy(): (String) -> Unit {
        return currentStrategy::install
    }

    fun isDefault(): Boolean {
        return currentStrategy is DefaultStrategy
    }

    fun setCurrentStrategyByName(name: String) {
        strategies.find { it.name().equals(name, ignoreCase = true) }
            ?.let { currentStrategy = it }
            ?: throw StrategyNotFoundException(name)
    }
}