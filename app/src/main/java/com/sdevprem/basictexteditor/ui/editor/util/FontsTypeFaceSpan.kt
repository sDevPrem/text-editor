package com.sdevprem.basictexteditor.ui.editor.util

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan


class FontsTypeFaceSpan(
    val fontName: String,
    private val fontTypeface: Typeface
) : MetricAffectingSpan() {

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
        val fake = oldStyle and fontTypeface.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.typeface = fontTypeface
    }
}