package com.sdevprem.basictexteditor.domain.model

import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange

data class NoteWithStyle(
    val title: String = "",
    val body: String = "",
    val ranges: List<SpanStyleRange> = emptyList(),
    val id: Long = 0
)