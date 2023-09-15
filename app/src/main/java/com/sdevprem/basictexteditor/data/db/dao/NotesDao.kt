package com.sdevprem.basictexteditor.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sdevprem.basictexteditor.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM note WHERE :id = id")
    suspend fun deleteNote(id: Long)

    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE :id = id")
    fun getNoteById(id: Long): Flow<Note>
}