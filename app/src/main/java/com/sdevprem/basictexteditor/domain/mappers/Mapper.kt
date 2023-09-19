package com.sdevprem.basictexteditor.domain.mappers

import com.sdevprem.basictexteditor.common.NoteUtils.filterInvalids
import com.sdevprem.basictexteditor.data.model.Format
import com.sdevprem.basictexteditor.data.model.FormatType
import com.sdevprem.basictexteditor.data.model.Note
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.domain.provider.DrawableProvider
import com.sdevprem.basictexteditor.domain.provider.FontProvider
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.FontTypeStyle
import com.sdevprem.basictexteditor.ui.editor.util.ImageStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.RelativeFontSizeStyle
import com.sdevprem.basictexteditor.ui.editor.util.StyleRange
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle

fun StyleRange.toFormat(): Format {
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
        is FontTypeStyle -> Format(start, end, FormatType.FONT_TYPE, listOf(style.data))
    }
}

fun Format.toStyleRange(
    drawableProvider: DrawableProvider,
    fontProvider: FontProvider
) = when (formatType) {
    FormatType.BOLD -> StyleRange(BoldStyle, end, start)
    FormatType.ITALICS -> StyleRange(ItalicStyle, end, start)
    FormatType.UNDERLINE -> StyleRange(UnderLineStyle, end, start)
    FormatType.FONT_SIZE -> StyleRange(RelativeFontSizeStyle(data[0].toFloat()), end, start)
    FormatType.IMAGE -> StyleRange(ImageStyle(data[0], drawableProvider), end, start)
    FormatType.FONT_TYPE -> StyleRange(FontTypeStyle(data[0], fontProvider), end, start)
}

fun Note.toNoteWithStyle(
    drawableProvider: DrawableProvider,
    fontProvider: FontProvider,
) = NoteWithStyle(
    title = title,
    body = body,
    ranges = formatList.map { it.toStyleRange(drawableProvider, fontProvider) },
    id = id
)

fun NoteWithStyle.toNote() = Note(
    title = title,
    body = body,
    //filter invalid spans while saving
    formatList = ranges.filterInvalids(body).map { it.toFormat() },
    id = id
)