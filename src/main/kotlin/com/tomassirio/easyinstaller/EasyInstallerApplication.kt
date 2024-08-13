package com.tomassirio.easyinstaller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.CommandScan

@SpringBootApplication
@CommandScan
class EasyInstallerApplication

fun main(args: Array<String>) {
	runApplication<EasyInstallerApplication>(*args)
}
