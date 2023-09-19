package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Typeface
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan


sealed class Range(
    var start: Int,
    var end: Int,
    val format: Any
)

class StyleRange(
    val style: Style,
    end: Int,
    start: Int,
    format: Any = style.createFormat()
) : Range(start, end, format)

fun StyleRange.copy(
    start: Int = this.start,
    end: Int = this.end,
    style: Style = this.style,
) = StyleRange(style, end, start)

fun StyleRange.deepCopy(
    start: Int = this.start,
    end: Int = this.end,
    style: Style = this.style,
    format: Any = this.format
) = StyleRange(style, end, start, format)

fun Style.createFormat() = when (this) {
    is BoldStyle -> StyleSpan(Typeface.BOLD)
    is ItalicStyle -> StyleSpan(Typeface.ITALIC)
    is UnderLineStyle -> UnderlineSpan()
    is RelativeFontSizeStyle -> RelativeSizeSpan(sizeMultiplier)
    is ImageStyle -> ImageSpan(getDrawable())
    is FontTypeStyle -> FontTypeFaceSpan(getFontTypeFace())
}

