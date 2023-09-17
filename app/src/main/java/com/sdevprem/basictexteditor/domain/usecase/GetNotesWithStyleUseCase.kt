package com.sdevprem.basictexteditor.domain.usecase

import com.sdevprem.basictexteditor.common.provider.DrawableProvider
import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.mappers.toNoteWithStyle
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNotesWithStyleUseCase @Inject constructor(
    private val repository: NotesRepository,
    private val drawableProvider: DrawableProvider
) {
    operator fun invoke(): Flow<List<NoteWithStyle>> = repository.getNotes()
        .map { notes ->
            notes.map { it.toNoteWithStyle(drawableProvider) }
        }
}