package com.sdevprem.basictexteditor.ui.editor

import android.text.Spannable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.domain.usecase.GetNoteWithStyleUseCase
import com.sdevprem.basictexteditor.domain.usecase.SaveNoteWithStyleUseCase
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.SimpleStyle
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange
import com.sdevprem.basictexteditor.ui.editor.util.Style
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle
import com.sdevprem.basictexteditor.ui.editor.util.copy
import com.sdevprem.basictexteditor.ui.editor.util.isAlreadyFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    getNoteWithStyle: GetNoteWithStyleUseCase,
    private val saveNoteWithStyleUseCase: SaveNoteWithStyleUseCase,
    private val repository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _editorAction = MutableSharedFlow<EditorViewAction>()
    val editorAction = _editorAction.asSharedFlow()
    private val id: Long

    init {
        id = EditorFragmentArgs.fromSavedStateHandle(savedStateHandle).id
        if (id > 0)
            getNoteWithStyle(id)
                .onEach {
                    _editorAction.emit(EditorViewAction.ShowNote(it))
                    _ranges.clear()
                    _ranges.addAll(it.ranges)
                }
                .launchIn(viewModelScope)
    }

    private val _ranges: MutableList<SpanStyleRange> = mutableListOf()
    val ranges: List<SpanStyleRange> = _ranges

    private fun removeFormatting(spannable: Spannable, style: Style, start: Int, end: Int) {
        when (style) {
            is BoldStyle -> spannable.removeFormatting<BoldStyle>(_ranges, start, end)
            ItalicStyle -> spannable.removeFormatting<ItalicStyle>(_ranges, start, end)
            UnderLineStyle -> spannable.removeFormatting<UnderLineStyle>(_ranges, start, end)
            else -> {}
        }
    }

    fun updateRanges(start: Int, countAfter: Int, countBefore: Int) {
        val delta = countAfter - countBefore
        for (range in ranges) {
            if (start <= range.start && start + countBefore >= range.end) {
                //text :   -------------------
                //range :       --------
                //changing:  --------------
                range.start = start
                range.end = start
            } else if (start < range.start && start + countBefore < range.start) {
                //text :   -------------------
                //range :         --------
                //changing:  ---
                range.start += delta
                range.end += delta
            } else if (start < range.start && start + countBefore > start) {
                //text :   -------------------
                //range :         --------
                //changing:   ------
                range.start = start
                range.end += delta
            } else {
                range.end += delta
            }
        }
    }

    fun toggleFormatting(style: SimpleStyle, spannable: Spannable, start: Int, end: Int) {
        if (start == end)
            return

        if (spannable.isAlreadyFormatted(start, end, style)) {
            removeFormatting(spannable, style, start, end)
        } else {
            removeFormatting(spannable, style, start, end)
            val range = SpanStyleRange(start, end, style)
            spannable.setSpan(range.format, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            _ranges.add(range)
        }

    }

    private inline fun <reified T : Style> Spannable.removeFormatting(
        formattingRanges: MutableList<SpanStyleRange>,
        start: Int,
        end: Int,
    ) {
        val newRanges: MutableList<SpanStyleRange> = ArrayList()
        val removeIndex = mutableListOf<Int>()

        for (i in formattingRanges.indices) {
            val range = formattingRanges[i]
            if (range.style !is T)
                continue

            if (range.start >= end || range.end <= start) {
                //range: --         --
                //selection:   ---
                // Range is outside the selected text, no change needed
                continue
            } else if (range.start >= start && range.end <= end) {
                //range:       ------
                //selection: -----------
                //Range is completely inside the selected text, remove it
                removeSpan(range.format)
                removeIndex.add(i)
            } else if (range.start < start && range.end > end) {
                //range:       ------------
                //selection:     -------
                //Reminder:    --        --
                // Range surrounds the selected text, split it into two
                removeSpan(range.format)
                val secondEnd = range.end
                range.end = start
                setSpan(range.format, range.start, range.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                val secondRange = range.copy(end, secondEnd)
                setSpan(
                    secondRange.format,
                    secondRange.start,
                    secondRange.end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                newRanges.add(secondRange)
            } else if (range.start < start) {
                //range:       -----------
                //selection:     ------------
                //Reminder:    --
                // Range overlaps the start of the selected text
                setSpan(
                    range.format,
                    range.start,
                    start,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                range.end = start
            } else {
                //range:       -----------
                //selection: -----------
                //Reminder:             --
                //Range overlaps the end of the selected text
                setSpan(
                    range.format,
                    end,
                    range.end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                range.start = end
            }
        }
        removeIndex.forEach {
            if (it in formattingRanges.indices)
                formattingRanges.removeAt(it)
        }
        formattingRanges.addAll(newRanges)
    }

    fun saveNote(title: String, body: String) {
        val noteWithStyle = NoteWithStyle(
            title = title,
            body = body,
            ranges = ranges,
            id = if (id > 0) id else 0
        )
        viewModelScope.launch {
            saveNoteWithStyleUseCase(noteWithStyle)
            _editorAction.emit(EditorViewAction.NavigateBack)
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            repository.deleteNote(id)
            _editorAction.emit(EditorViewAction.NavigateBack)
        }
    }

}

sealed interface EditorViewAction {
    class ShowNote(val noteWithStyle: NoteWithStyle) : EditorViewAction
    object NavigateBack : EditorViewAction
}