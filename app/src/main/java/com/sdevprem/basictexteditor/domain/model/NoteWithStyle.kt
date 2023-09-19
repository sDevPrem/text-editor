package com.sdevprem.basictexteditor.domain.model

import com.sdevprem.basictexteditor.ui.editor.util.StyleRange

data class NoteWithStyle(
    val title: String = "",
    val body: String = "",
    val ranges: List<StyleRange> = emptyList(),
    val id: Long = 0
)