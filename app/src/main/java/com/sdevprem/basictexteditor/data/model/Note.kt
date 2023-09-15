package com.sdevprem.basictexteditor.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sdevprem.basictexteditor.data.mapper.FormatConverter

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val title: String,
    val body: String,
    @TypeConverters(FormatConverter::class)
    val formatList: List<Format>
)