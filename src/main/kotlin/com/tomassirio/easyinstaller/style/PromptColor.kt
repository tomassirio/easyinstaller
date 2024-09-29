package com.tomassirio.easyinstaller.style

enum class PromptColor(private val value: Int) {
    BLACK(0),
    RED(1),
    GREEN(2),
    YELLOW(3),
    BLUE(4),
    MAGENTA(5),
    CYAN(6),
    WHITE(7),
    BRIGHT(8),
    LIGHT_PINK(9);

    fun toJlineAttributedStyle(): Int {
        return this.value
    }
}