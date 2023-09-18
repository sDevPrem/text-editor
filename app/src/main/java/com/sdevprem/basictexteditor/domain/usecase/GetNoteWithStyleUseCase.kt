package com.sdevprem.basictexteditor.domain.usecase

import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.mappers.toNoteWithStyle
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.domain.provider.DrawableProvider
import com.sdevprem.basictexteditor.domain.provider.FontProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNoteWithStyleUseCase @Inject constructor(
    private val repository: NotesRepository,
    private val drawableProvider: DrawableProvider,
    private val fontProvider: FontProvider,
) {

    /**
     * Fetch and returns a [Flow] containing [NoteWithStyle]
     * which corresponds to the given [id] or null if it doesn't
     * exist
     */
    operator fun invoke(id: Long): Flow<NoteWithStyle?> = repository.getNoteById(id)
        .map { it?.toNoteWithStyle(drawableProvider, fontProvider) }
}