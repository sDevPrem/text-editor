package com.sdevprem.basictexteditor.data.mapper

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sdevprem.basictexteditor.data.model.Format


object FormatConverter {

    @TypeConverter
    fun convertFormatsToString(list: List<Format>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun convertStringToFormats(formatString: String): List<Format> {
        return Gson().fromJson(formatString, object : TypeToken<List<Format>>() {}.type)
    }
}