package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.text.getSpans

fun Spannable.isAlreadyFormatted(start: Int, end: Int, styleType: SimpleStyle): Boolean {
    return when (styleType) {
        is BoldStyle -> hasStyle(start, end, Typeface.BOLD)
        is ItalicStyle -> hasStyle(start, end, Typeface.ITALIC)
        is UnderLineStyle -> isUnderlined(start, end)
    }
}

fun Spannable.isUnderlined(start: Int, end: Int): Boolean {
    for (i in start until end) {
        if (getSpans<UnderlineSpan>(i, i + 1).isEmpty())
            return false
    }
    return true
}

fun Spannable.hasStyle(start: Int, end: Int, style: Int): Boolean {
    for (i in start until end) {
        val matched = getSpans<StyleSpan>(i, i + 1).any { it.style == style }
        if (!matched)
            return false
    }
    return true
}