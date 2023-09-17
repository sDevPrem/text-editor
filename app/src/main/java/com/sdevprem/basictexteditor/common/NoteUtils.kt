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
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalDrawable = Drawable.createFromStream(inputStream, uri.toString())

        // Calculate the device width
        val displayMetrics = context.resources.displayMetrics
        val deviceWidth = displayMetrics.widthPixels - 32.dpToPx

        // Calculate the new bounds based on device width
        val originalWidth = originalDrawable?.intrinsicWidth
            ?: return AppCompatResources.getDrawable(context, R.drawable.ic_img)!!


        val newWidth: Float
        val scaleFactor = if (originalWidth > deviceWidth) {
            newWidth = deviceWidth
            deviceWidth / originalWidth
        } else {
            newWidth = originalWidth.toFloat()
            1.0f
        }

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
}

