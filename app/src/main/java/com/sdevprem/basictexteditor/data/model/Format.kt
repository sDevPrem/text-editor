package com.sdevprem.basictexteditor.data.model

data class Format(
    val start: Int,
    val end: Int,
    val formatType: FormatType,
    val data: List<String> = emptyList(),
)

enum class FormatType {
    BOLD, ITALICS, UNDERLINE, FONT_SIZE, IMAGE, FONT_TYPE
}

