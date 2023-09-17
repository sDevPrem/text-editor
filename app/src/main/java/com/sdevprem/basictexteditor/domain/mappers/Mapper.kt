package com.sdevprem.basictexteditor.domain.mappers

import com.sdevprem.basictexteditor.common.NoteUtils.filterInvalids
import com.sdevprem.basictexteditor.common.provider.DrawableProvider
import com.sdevprem.basictexteditor.data.model.Format
import com.sdevprem.basictexteditor.data.model.FormatType
import com.sdevprem.basictexteditor.data.model.Note
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.ImageStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.RelativeFontSizeStyle
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle

fun SpanStyleRange.toFormat(): Format {
    return when (style) {
        is BoldStyle -> Format(start, end, FormatType.BOLD)
        is ItalicStyle -> Format(start, end, FormatType.ITALICS)
        is UnderLineStyle -> Format(start, end, FormatType.UNDERLINE)
        is RelativeFontSizeStyle -> Format(
            start,
            end,
            FormatType.FONT_SIZE,
            listOf(style.data.toString())
        )

        is ImageStyle -> Format(start, end, FormatType.IMAGE, listOf(style.data))
    }
}

fun Format.toStyleSpanRange(
    drawableProvider: DrawableProvider
) = when (formatType) {
    FormatType.BOLD -> SpanStyleRange(start, end, BoldStyle)
    FormatType.ITALICS -> SpanStyleRange(start, end, ItalicStyle)
    FormatType.UNDERLINE -> SpanStyleRange(start, end, UnderLineStyle)
    FormatType.FONT_SIZE -> SpanStyleRange(start, end, RelativeFontSizeStyle(data[0].toFloat()))
    FormatType.IMAGE -> SpanStyleRange(start, end, ImageStyle(data[0], drawableProvider))
}

fun Note.toNoteWithStyle(
    drawableProvider: DrawableProvider
) = NoteWithStyle(
    title = title,
    body = body,
    ranges = formatList.map { it.toStyleSpanRange(drawableProvider) },
    id = id
)

fun NoteWithStyle.toNote() = Note(
    title = title,
    body = body,
    //filter invalid spans while saving
    formatList = ranges.filterInvalids(body).map { it.toFormat() },
    id = id
)