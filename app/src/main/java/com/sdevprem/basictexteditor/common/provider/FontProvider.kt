package com.sdevprem.basictexteditor.common.provider

import android.graphics.Typeface

interface FontProvider {
    fun getFont(fontName: String): Typeface
}