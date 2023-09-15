package com.sdevprem.basictexteditor.domain.usecase

import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.mappers.toNote
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveNoteWithStyleUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteWithStyle: NoteWithStyle) {
        if (noteWithStyle.id > 0)
            repository.updateNote(noteWithStyle.toNote())
        else repository.insertNote(noteWithStyle.toNote())
    }

}