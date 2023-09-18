package com.sdevprem.basictexteditor.common

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import com.sdevprem.basictexteditor.R
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange

object NoteUtils {
    fun List<SpanStyleRange>.filterInvalids(text: String) = filter {
        it.start < it.end &&
                it.end <= text.length
    }

    fun loadDrawableWithWidthConstraint(uri: Uri, context: Context): Drawable {
        val originalDrawable = context.contentResolver.openInputStream(uri)?.use {
            Drawable.createFromStream(it, uri.toString())
        } ?: return AppCompatResources.getDrawable(context, R.drawable.ic_img)!!

        // Calculate the device width
        val displayMetrics = context.resources.displayMetrics
        val deviceWidth = displayMetrics.widthPixels - 72.dpToPx

        // Calculate the new bounds based on device width
        val originalWidth = originalDrawable.intrinsicWidth

        val scaleFactor = deviceWidth / originalWidth
        val newWidth: Float = deviceWidth

        val originalHeight = originalDrawable.intrinsicHeight
        val newHeight = (originalHeight * scaleFactor).toInt()

        originalDrawable.setBounds(0, 0, newWidth.toInt(), newHeight)

        return originalDrawable
    }

    val Number.dpToPx: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        )

    const val FONT_NUNITO = "Nunito-Regular.ttf"
    const val FONT_ROBOTO_SLAB = "RobotoSlab-Regular.ttf"
    val fonts = listOf(
        FONT_NUNITO,
        FONT_ROBOTO_SLAB
    )
}

