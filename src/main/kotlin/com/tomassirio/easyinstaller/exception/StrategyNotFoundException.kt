package com.tomassirio.easyinstaller.exception

class StrategyNotFoundException(name: String) : RuntimeException("Strategy with name '$name' not found")