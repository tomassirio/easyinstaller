package com.tomassirio.easyinstaller.config

import com.tomassirio.easyinstaller.config.helper.OSArchUtil
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment

class OsBasedProfileActivator(
        private val osArchUtil: OSArchUtil = OSArchUtil()
): EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val os = osArchUtil.getOS()

        environment.addActiveProfile(os)
    }
}

