package com.sdevprem.basictexteditor.ui.editor

import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.basictexteditor.data.repository.NotesRepository
import com.sdevprem.basictexteditor.domain.model.NoteWithStyle
import com.sdevprem.basictexteditor.domain.provider.DrawableProvider
import com.sdevprem.basictexteditor.domain.provider.FontProvider
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
    savedStateHandle: SavedStateHandle,
    private val saveNoteWithStyleUseCase: SaveNoteWithStyleUseCase,
    private val repository: NotesRepository,
    private val drawableProvider: DrawableProvider,
    private val fontProvider: FontProvider,
) : ViewModel() {

    private val _editorAction = MutableSharedFlow<EditorViewAction>()
    val editorAction = _editorAction.asSharedFlow()

    private val id: Long

    private val _ranges: MutableList<SpanStyleRange> = mutableListOf()
    private val ranges: List<SpanStyleRange> = _ranges

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

    /**
     * Removes any font exist between [start] and [end]
     * from the [spannable]
     */
    fun removeFont(spannable: Spannable, start: Int, end: Int) {
        spannable.removeFormatting(_ranges, start, end) {
            //skip non font styles
            !FontTypeStyle::class.java.isInstance(it)
        }
    }

    /**
     * Insert the image corresponding to the [uri] in the [spannable] from the [selectionStart]
     * to the [selectionEnd] in a new line and return it.
     */
    fun insertImage(
        uri: Uri,
        spannable: Spannable,
        selectionStart: Int,
        selectionEnd: Int
    ): Spannable {

        return SpannableStringBuilder(spannable).apply {
            val newLine = "\n"
            val id = " "
            val totalLength = 2 * newLine.length + id.length
            val style = ImageStyle(uri.toString(), drawableProvider)
            val range =
                SpanStyleRange(
                    selectionStart + newLine.length,
                    selectionStart + id.length + newLine.length,
                    style
                )

            //replace the selection with new line
            replace(selectionStart, selectionEnd, newLine + id + newLine)

            //update the ranges according to new line entered above
            updateRanges(selectionStart, totalLength, 0)

            //remove any formatting from the selection
            removeFormatting(
                _ranges,
                selectionStart,
                selectionStart + totalLength
            ) { false /*remove all style*/ }

            //insert image
            setSpan(range.format, range.start, range.end, style.spannableFlag)

            //add the range
            _ranges.add(range)
        }
    }

    /**
     * Updates ranges according to where and how many
     * character changed in the text.
     * [start] : The start index from where user started entering text
     * [countAfter] : The count of the character user entered
     * [countBefore] : The count of the character that have been replaced
     */
    fun updateRanges(start: Int, countAfter: Int, countBefore: Int) {
        val delta = countAfter - countBefore
        val selectionEnd = start + countBefore

        _ranges.removeIf { range ->
            if (range.start >= selectionEnd) {
                //range :            ------------
                //change:       -----
                //reminder :              ------------
                //user has changed some text before the range
                //move range accordingly

                range.start += delta
                range.end += delta

            } else if (start > range.end) {
                return@removeIf false
            } else if (start >= range.start && selectionEnd <= range.end) {
                //range :         ------------
                //selection:        --------
                //user has changed text within range
                //apply the change to the range.end

                //do not expand non expandable
                if (
                    !(start == range.end &&
                            (range.style.spannableFlag == Spannable.SPAN_EXCLUSIVE_EXCLUSIVE))
                ) {
                    range.end += delta

                    //if the range become empty, remove it
                    if (range.start == range.end)
                        return@removeIf true
                }

            } else if (start < range.start && selectionEnd > range.end) {
                //range :        ------------
                //selection:   ----------------
                //reminder :
                //user has selected all the character comes under
                //the range and replaced with other text
                //remove the range

                return@removeIf true

            } else if (start < range.start && selectionEnd < range.end) {
                //range :        ------------
                //selection:  ---------
                //reminder :            _____
                //user selection overlaps the start of range
                //move range.start to the last character of the countAfter
                //and add the changes to the end

                range.start = start + countAfter
                range.end += delta

            } else if (start < range.end && selectionEnd > range.end) {
                //range :       ------------
                //selection:         ---------
                //reminder :    _____
                //user selection overlaps the end of the range
                //move range.end to the last character from the start of the selection
                //because the span will expand to the end of the user changed text

                range.end = start + countAfter
            }
            return@removeIf false
        }
    }

    /**
     * Remove the [style] if it exist between [selectionStart] and [selectionEnd] else
     * add that [style] between that range.
     */
    fun toggleFormatting(
        style: Style,
        spannable: Spannable,
        selectionStart: Int,
        selectionEnd: Int
    ) {
        if (selectionStart == selectionEnd)
            return

        if (style.isSpannableFormatted(selectionStart, selectionEnd, spannable)) {
            //if range is fully formatted with the style then remove only
            removeFormatting(spannable, style, selectionStart, selectionEnd)
        } else {
            //if range is not fully formatted with style, then remove it first
            //and then apply it again
            removeFormatting(spannable, style, selectionStart, selectionEnd)
            val range = SpanStyleRange(selectionStart, selectionEnd, style)
            spannable.setSpan(range.format, selectionStart, selectionEnd, style.spannableFlag)
            _ranges.add(range)
        }

    }

    /**
     * Applies the font corresponding to the [fontName] from [start] to [end]
     * in the [spannable]
     */
    fun applyFont(start: Int, end: Int, spannable: Spannable, fontName: String) {
        toggleFormatting(FontTypeStyle(fontName, fontProvider), spannable, start, end)
    }

    /**
     * Removes the [style] if exist between [start] and [end] from the [spannable]
     */
    private fun removeFormatting(spannable: Spannable, style: Style, start: Int, end: Int) {
        spannable.removeFormatting(_ranges, start, end) {
            !style::class.java.isInstance(it)
        }
    }

    /**
     * Removes formatting from the given [formattingRanges]
     * [formattingRanges] : The list of range in which the formatting need to be removed.
     * [selectionStart] : The start of the selection.
     * [selectionEnd] : The end of the selection.
     * [skip] : Should returns false to skip the given style for further checking,
     * else true to check and remove.
     */
    private fun Spannable.removeFormatting(
        formattingRanges: MutableList<SpanStyleRange>,
        selectionStart: Int,
        selectionEnd: Int,
        skip: (Style) -> Boolean
    ) {
        val newRanges: MutableList<SpanStyleRange> = ArrayList()
        formattingRanges.removeIf { range ->
            if (skip(range.style))
                return@removeIf false

            if (range.start >= selectionEnd || range.end <= selectionStart) {
                //range: --         --
                //selection:   ---
                // Range is outside the selected text, no change needed
                return@removeIf false
            } else if (range.start >= selectionStart && range.end <= selectionEnd) {
                //range:       ------
                //selection: -----------
                //Range is completely inside the selected text, remove it
                removeSpan(range.format)
                return@removeIf true
            } else if (range.start < selectionStart && range.end > selectionEnd) {
                //range:       ------------
                //selection:     -------
                //Reminder:    --        --
                // Range surrounds the selected text, split it into two

                //remove all the format of the range
                removeSpan(range.format)

                //the end of the second range
                val secondEnd = range.end
                //move end of the first range to the start of the selection
                range.end = selectionStart
                //copy that range from selection end to second end
                val secondRange = range.copy(selectionEnd, secondEnd)

                //now apply both ranges
                setSpan(range.format, range.start, range.end, range.style.spannableFlag)
                setSpan(
                    secondRange.format,
                    secondRange.start,
                    secondRange.end,
                    range.style.spannableFlag
                )
                newRanges.add(secondRange)
            } else if (range.start < selectionStart) {
                //range:       -----------
                //selection:     ------------
                //Reminder:    --
                //Range overlaps the start of the selected text
                //Make its end to the start of the selection

                setSpan(
                    range.format,
                    range.start,
                    selectionStart,
                    range.style.spannableFlag
                )
                range.end = selectionStart
            } else {
                //range:       -----------
                //selection: -----------
                //Reminder:             --
                //Range overlaps the end of the selected text
                //Make its start to the end of the selection
                setSpan(
                    range.format,
                    selectionEnd,
                    range.end,
                    range.style.spannableFlag
                )
                range.start = selectionEnd
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