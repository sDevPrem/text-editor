package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.net.toUri
import androidx.core.text.getSpans
import com.sdevprem.basictexteditor.domain.provider.DrawableProvider
import com.sdevprem.basictexteditor.domain.provider.FontProvider

sealed class Style {
    abstract val spannableFlag: Int
}

/**
 * Marker class for complex styles that operates on some parameter
 */
sealed class ParameterStyle : Style()

/**
 * Style class that can be toggled using [SimpleStyle.isSpannableFormatted]
 */
sealed class SimpleStyle : Style() {
    abstract fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean
}

object BoldStyle : SimpleStyle() {

    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE

    override fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean {
        for (i in start until end) {
            val matched = spannable.getSpans<StyleSpan>(i, i + 1).any { it.style == Typeface.BOLD }
            if (!matched)
                return false
        }
        return true
    }
}

object ItalicStyle : SimpleStyle() {

    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE
    override fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean {
        for (i in start until end) {
            val matched =
                spannable.getSpans<StyleSpan>(i, i + 1).any { it.style == Typeface.ITALIC }
            if (!matched)
                return false
        }
        return true
    }
}

object UnderLineStyle : SimpleStyle() {
    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE
    override fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean {
        for (i in start until end) {
            if (spannable.getSpans<UnderlineSpan>(i, i + 1).isEmpty())
                return false
        }
        return true
    }

}

data class FontTypeStyle(
    val fontName: String,
    private val fontProvider: FontProvider
) : ParameterStyle() {

    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE

    fun getFontTypeFace() = fontProvider.getFont(fontName)
}

data class RelativeFontSizeStyle(val sizeMultiplier: Float) : ParameterStyle() {

    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE

    companion object {
        const val DEFAULT_RELATIVE_SIZE = 1f
    }
}

data class ImageStyle(
    val imgUri: String,
    private val drawableProvider: DrawableProvider
) : ParameterStyle() {

    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

    fun getDrawable() = drawableProvider.getDrawable(imgUri.toUri())

}
