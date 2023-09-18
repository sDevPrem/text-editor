package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import com.sdevprem.basictexteditor.domain.provider.FontProvider


class FontsTypeFace(
    val fontName: String,
    fontProvider: FontProvider
) : MetricAffectingSpan() {

    private val tf = fontProvider.getFont(fontName)

    override fun updateDrawState(ds: TextPaint?) {
        applyCustomTypeFace(ds!!)
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        applyCustomTypeFace(textPaint)
    }

    private fun applyCustomTypeFace(paint: Paint) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0
        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.typeface = tf
    }
}