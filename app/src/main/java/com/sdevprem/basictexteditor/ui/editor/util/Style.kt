package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.net.toUri
import androidx.core.text.getSpans
import com.sdevprem.basictexteditor.domain.provider.DrawableProvider
import com.sdevprem.basictexteditor.domain.provider.FontProvider

sealed class Style {
    abstract val spannableFlag: Int
    abstract fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean
}

sealed class ParameterStyle<T>(val data: T) : Style()

sealed class SimpleStyle : Style()

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

class FontTypeStyle(
    fontName: String,
    private val fontProvider: FontProvider
) : ParameterStyle<String>(fontName) {

    private val fontName = data
    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE

    override fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean {
        for (i in start until end) {
            val matched =
                spannable.getSpans<FontsTypeFaceSpan>(i, i + 1).any { it.fontName == fontName }
            if (!matched)
                return false
        }
        return true
    }

    fun getFontTypeFace() = fontProvider.getFont(fontName)
}

class RelativeFontSizeStyle(sizeMultiplier: Float) : ParameterStyle<Float>(sizeMultiplier) {

    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_INCLUSIVE
    private val defaultValue = 1f
    private val multiplier = sizeMultiplier

    override fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean {
        var isFormatted = false

        //if the multiplier is default value
        //the spannable is said to be formatted only when
        //it has a text which has multiplier not equal to default-value
        if (multiplier == defaultValue) {
            for (i in start until end) {
                if (
                    spannable.getSpans<RelativeSizeSpan>(i, i + 1).isEmpty() ||
                    spannable.getSpans<RelativeSizeSpan>(i, i + 1).any {
                        it.sizeChange != defaultValue
                    }
                ) {
                    isFormatted = true
                    break
                }
            }
        } else {
            //if the multiplier is not default value
            //the spannable is said to be formatted only when
            //its all text will have a size multiplier equal to this.multiplier
            isFormatted = true
            for (i in start until end) {
                if (
                    spannable.getSpans<RelativeSizeSpan>(i, i + 1).isEmpty() ||
                    spannable.getSpans<RelativeSizeSpan>(i, i + 1)
                        .any { it.sizeChange != multiplier }
                ) {
                    isFormatted = false
                    break
                }
            }
        }
        return isFormatted
    }
}

class ImageStyle(
    private val imgUri: String,
    private val drawableProvider: DrawableProvider
) : ParameterStyle<String>(imgUri) {
    override val spannableFlag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    override fun isSpannableFormatted(start: Int, end: Int, spannable: Spannable): Boolean {
        for (i in start until end) {
            if (spannable.getSpans<ImageSpan>(i, i + 1).isEmpty())
                return false
        }
        return true
    }

    fun getDrawable() = drawableProvider.getDrawable(imgUri.toUri())

}
