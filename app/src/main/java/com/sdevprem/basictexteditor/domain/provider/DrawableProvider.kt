package com.sdevprem.basictexteditor.domain.provider

import android.graphics.drawable.Drawable
import android.net.Uri

interface DrawableProvider {
    fun getDrawable(uri: Uri): Drawable
}