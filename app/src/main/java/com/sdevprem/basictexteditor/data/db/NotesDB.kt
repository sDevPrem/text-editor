package com.sdevprem.basictexteditor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sdevprem.basictexteditor.data.db.dao.NotesDao
import com.sdevprem.basictexteditor.data.mapper.FormatConverter
import com.sdevprem.basictexteditor.data.model.Note

@Database(entities = [Note::class], version = 1)
@TypeConverters(FormatConverter::class)
abstract class NotesDB : RoomDatabase() {

    abstract fun getNotesDao(): NotesDao
}