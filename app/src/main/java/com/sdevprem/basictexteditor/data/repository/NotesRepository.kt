package com.sdevprem.basictexteditor.data.repository

import com.sdevprem.basictexteditor.data.db.dao.NotesDao
import com.sdevprem.basictexteditor.data.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val dao: NotesDao
) {

    suspend fun insertNote(note: Note) = dao.insertNote(note)

    suspend fun updateNote(note: Note) = dao.updateNote(note)

    suspend fun deleteNote(id: Long) = dao.deleteNote(id)

    fun getNotes(): Flow<List<Note>> = dao.getNotes()

    fun getNoteById(id: Long): Flow<Note> = dao.getNoteById(id)

}