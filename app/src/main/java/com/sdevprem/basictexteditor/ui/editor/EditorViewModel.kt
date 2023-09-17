package com.sdevprem.basictexteditor.ui.editor

import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.basictexteditor.common.provider.DrawableProvider
import com.sdevprem.basictexteditor.common.provider.FontProvider
import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.domain.usecase.GetNoteWithStyleUseCase
import com.sdevprem.basictexteditor.domain.usecase.SaveNoteWithStyleUseCase
import com.sdevprem.basictexteditor.ui.editor.util.FontTypeStyle
import com.sdevprem.basictexteditor.ui.editor.util.ImageStyle
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange
import com.sdevprem.basictexteditor.ui.editor.util.Style
import com.sdevprem.basictexteditor.ui.editor.util.copy
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
    savedStateHandle: SavedStateHandle,
    private val drawableProvider: DrawableProvider,
    private val fontProvider: FontProvider,
) : ViewModel() {

    private val _editorAction = MutableSharedFlow<EditorViewAction>()
    val editorAction = _editorAction.asSharedFlow()
    private val id: Long
    private var shouldUpdate = true

    init {
        id = EditorFragmentArgs.fromSavedStateHandle(savedStateHandle).id
        if (id > 0)
            getNoteWithStyle(id)
                .onEach {
                    _editorAction.emit(EditorViewAction.ShowNote(it ?: NoteWithStyle()))
                    _ranges.clear()
                    it?.let { _ranges.addAll(it.ranges) }

                }
                .launchIn(viewModelScope)
    }

    private val _ranges: MutableList<SpanStyleRange> = mutableListOf()
    private val ranges: List<SpanStyleRange> = _ranges

    private fun removeFormatting(spannable: Spannable, style: Style, start: Int, end: Int) {
        spannable.removeFormatting(_ranges, start, end, style::class.java)
    }

    fun removeFont(spannable: Spannable, start: Int, end: Int) {
        spannable.removeFormatting(_ranges, start, end, FontTypeStyle::class.java)
    }

    fun insertImage(uri: Uri, spannable: Spannable, start: Int, end: Int): Spannable {

        return SpannableStringBuilder(spannable).apply {
            val id = " "
            replace(start, end, id)
            val style = ImageStyle(uri.toString(), drawableProvider)
            val range = SpanStyleRange(start, start + id.length, style)
            setSpan(range.format, start, start + id.length, style.spannableFlag)
            updateRanges(start, start + id.length, 0)
            shouldUpdate = false
            _ranges.add(range)
        }
    }
    fun updateRanges(start: Int, countAfter: Int, countBefore: Int) {
        if (!shouldUpdate) {
            shouldUpdate = true
            return
        }
        val delta = countAfter - countBefore
        val selectionEnd = start + countBefore
        _ranges.removeIf { range ->
            if (range.start >= selectionEnd) {
                //range :            ------------
                //selection:   -----
                //reminder :              ------------
                range.start += delta
                range.end += delta
            } else if (start > range.end) {
                return@removeIf false
            } else if (start >= range.start && selectionEnd <= range.end) {
                //range :         ------------
                //selection:        --------

                //do not expand non expandable
                if (
                    !(start == range.end &&
                            (range.style.spannableFlag == Spannable.SPAN_EXCLUSIVE_EXCLUSIVE))
                ) {
                    range.end += delta
                    if (range.start == range.end)
                        return@removeIf true
                }

            } else if (start < range.start && selectionEnd > range.end) {
                //range :        ------------
                //selection:   ----------------
                //reminder :
                return@removeIf true
            } else if (start < range.start && selectionEnd < range.end) {
                //range :        ------------
                //selection:  ---------
                //reminder :            _____
                range.start += delta + (selectionEnd - range.start)
                range.end += delta
            } else if (start < range.end && selectionEnd > range.end) {
                //range :       ------------
                //selection:         ---------
                //reminder :    _____
                range.end += delta + (selectionEnd - range.end)
            }
            return@removeIf false
        }
    }

    fun toggleFormatting(style: Style, spannable: Spannable, start: Int, end: Int) {
        if (start == end)
            return

        if (style.isSpannableFormatted(start, end, spannable)) {
            removeFormatting(spannable, style, start, end)
        } else {
            removeFormatting(spannable, style, start, end)
            val range = SpanStyleRange(start, end, style)
            spannable.setSpan(range.format, start, end, style.spannableFlag)
            _ranges.add(range)
        }

    }

    fun applyFont(start: Int, end: Int, spannable: Spannable, fontName: String) {
        toggleFormatting(
            FontTypeStyle(fontName, fontProvider),
            spannable,
            start,
            end
        )
    }

    private fun <T : Style> Spannable.removeFormatting(
        formattingRanges: MutableList<SpanStyleRange>,
        start: Int,
        end: Int,
        clazz: Class<T>
    ) {
        val newRanges: MutableList<SpanStyleRange> = ArrayList()
        formattingRanges.removeIf { range ->
            if (!clazz.isInstance(range.style))
                return@removeIf false

            if (range.start >= end || range.end <= start) {
                //range: --         --
                //selection:   ---
                // Range is outside the selected text, no change needed
                return@removeIf false
            } else if (range.start >= start && range.end <= end) {
                //range:       ------
                //selection: -----------
                //Range is completely inside the selected text, remove it
                removeSpan(range.format)
                return@removeIf true
            } else if (range.start < start && range.end > end) {
                //range:       ------------
                //selection:     -------
                //Reminder:    --        --
                // Range surrounds the selected text, split it into two
                removeSpan(range.format)
                val secondEnd = range.end
                range.end = start
                setSpan(range.format, range.start, range.end, range.style.spannableFlag)
                val secondRange = range.copy(end, secondEnd)
                setSpan(
                    secondRange.format,
                    secondRange.start,
                    secondRange.end,
                    range.style.spannableFlag
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
                    range.style.spannableFlag
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
                    range.style.spannableFlag
                )
                range.start = end
            }
            return@removeIf false
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
            if (id > 0)
                repository.deleteNote(id)
            _editorAction.emit(EditorViewAction.NavigateBack)
        }
    }

}

sealed interface EditorViewAction {
    class ShowNote(val noteWithStyle: NoteWithStyle) : EditorViewAction
    object NavigateBack : EditorViewAction
}