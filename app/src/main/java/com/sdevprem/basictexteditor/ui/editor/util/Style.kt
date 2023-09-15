package com.sdevprem.basictexteditor.ui.editor.util

sealed class Style

sealed class ParameterStyle<T>(
    val data: T
) : Style()

sealed class SimpleStyle : Style()

object BoldStyle : SimpleStyle()
object ItalicStyle : SimpleStyle()
object UnderLineStyle : SimpleStyle()

//class FontTypeStyle(
//    fontType: String,
//    val assetManagerProvider : () -> AssetManager,
//): ParameterStyle<String>(fontType)

class FontSizeStyle(
    fontSize: Int
) : ParameterStyle<Int>(fontSize)
