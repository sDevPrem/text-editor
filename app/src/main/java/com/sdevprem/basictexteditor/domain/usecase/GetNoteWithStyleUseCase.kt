package com.sdevprem.basictexteditor.domain.usecase

import com.sdevprem.basictexteditor.common.provider.DrawableProvider
import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.mappers.toNoteWithStyle
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNoteWithStyleUseCase @Inject constructor(
    private val repository: NotesRepository,
    private val drawableProvider: DrawableProvider
) {
    operator fun invoke(id: Long) = repository.getNoteById(id)
        .map { it?.toNoteWithStyle(drawableProvider) }
}