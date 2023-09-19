package com.sdevprem.basictexteditor.common.provider

import android.content.Context
import android.graphics.Typeface
import com.sdevprem.basictexteditor.domain.provider.FontProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FontFamilyProviderImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : FontProvider {

    /**
     * Returns the [Typeface] corresponding to the
     * font of [fontName]
     */
    override fun getFont(fontName: String): Typeface =
        Typeface.createFromAsset(context.assets, fontName)

}