package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan


sealed class Range(
    var start: Int,
    var end: Int,
    val format: Any
)

class SpanStyleRange(
    start: Int,
    end: Int,
    val style: Style,
    format: Any = when (style) {
        is BoldStyle -> StyleSpan(Typeface.BOLD)
        is ItalicStyle -> StyleSpan(Typeface.ITALIC)
        is UnderLineStyle -> UnderlineSpan()
        is FontSizeStyle -> AbsoluteSizeSpan(style.data)
//        is FontTypeStyle -> Typeface.createFromAsset(
//            style.assetManagerProvider(),
//            style.data
//        )
    }
) : Range(
    start,
    end,
    format
)

fun SpanStyleRange.copy(
    start: Int = this.start,
    end: Int = this.end,
    style: Style = this.style,
) = SpanStyleRange(start, end, style)

fun SpanStyleRange.deepCopy(
    start: Int = this.start,
    end: Int = this.end,
    style: Style = this.style,
    format: Any = this.format
) = SpanStyleRange(start, end, style, format)

