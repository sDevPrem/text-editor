package com.sdevprem.basictexteditor.domain.provider

import android.graphics.Typeface

interface FontProvider {
    fun getFont(fontName: String): Typeface
}