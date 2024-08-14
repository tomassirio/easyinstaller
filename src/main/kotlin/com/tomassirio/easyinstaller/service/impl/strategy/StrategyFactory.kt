package com.tomassirio.easyinstaller.service.impl.strategy

import org.springframework.stereotype.Component

@Component
class StrategyFactory(
    private val brewStrategy: BrewStrategy,
    private val defaultStrategy: DefaultStrategy,
    private val aptStrategy: AptStrategy,
//    private val snapStrategy: SnapStrategy,
//    private val yumStrategy: YumStrategy,
//    private val chocolateyStrategy: ChocolateyStrategy,
//    private val scoopStrategy: ScoopStrategy,
//    private val wingetStrategy: WingetStrategy,
//    private val nixStrategy: NixStrategy,
//    private val condaStrategy: CondaStrategy
) {
    fun getStrategy(packageManager: String?): (String) -> Unit {
        return when (packageManager?.lowercase()) {
            "brew" -> brewStrategy::install
            "apt" -> aptStrategy::install
//            "snap" -> snapStrategy::install
//            "yum" -> yumStrategy::install
//            "chocolatey" -> chocolateyStrategy::install
//            "scoop" -> scoopStrategy::install
//            "winget" -> wingetStrategy::install
//            "nix" -> nixStrategy::install
//            "conda" -> condaStrategy::install
            else -> defaultStrategy::install // Default to curl
        }
    }
}