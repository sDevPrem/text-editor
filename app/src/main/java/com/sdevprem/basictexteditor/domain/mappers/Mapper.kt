package com.sdevprem.basictexteditor.domain.mappers

import com.sdevprem.basictexteditor.common.filterInvalids
import com.sdevprem.basictexteditor.data.model.Format
import com.sdevprem.basictexteditor.data.model.FormatType
import com.sdevprem.basictexteditor.data.model.Note
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.FontSizeStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle

fun SpanStyleRange.toFormat(): Format {
    return when (style) {
        is BoldStyle -> Format(start, end, FormatType.BOLD)
        is ItalicStyle -> Format(start, end, FormatType.ITALICS)
        is UnderLineStyle -> Format(start, end, FormatType.UNDERLINE)
        is FontSizeStyle -> Format(start, end, FormatType.FONT_SIZE, listOf(style.data.toString()))
    }
}

fun Format.toStyleSpanRange() = when (formatType) {
    FormatType.BOLD -> SpanStyleRange(start, end, BoldStyle)
    FormatType.ITALICS -> SpanStyleRange(start, end, ItalicStyle)
    FormatType.UNDERLINE -> SpanStyleRange(start, end, UnderLineStyle)
    FormatType.FONT_SIZE -> SpanStyleRange(start, end, FontSizeStyle(data[0].toInt()))
}

fun Note.toNoteWithStyle() = NoteWithStyle(
    title = title,
    body = body,
    ranges = formatList.map { it.toStyleSpanRange() },
    id = id
)

fun NoteWithStyle.toNote() = Note(
    title = title,
    body = body,
    //filter invalid spans while saving
    formatList = ranges.filterInvalids(body).map { it.toFormat() },
    id = id
)