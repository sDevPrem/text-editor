package com.sdevprem.basictexteditor.common.provider

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import com.sdevprem.basictexteditor.common.NoteUtils
import com.sdevprem.basictexteditor.domain.provider.DrawableProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawableProviderImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : DrawableProvider {

    override fun getDrawable(uri: Uri): Drawable = NoteUtils
        .loadDrawableWithWidthConstraint(uri, context)

}