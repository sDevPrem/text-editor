package com.sdevprem.basictexteditor.ui.editor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sdevprem.basictexteditor.R
import com.sdevprem.basictexteditor.common.NoteUtils
import com.sdevprem.basictexteditor.common.animateDown
import com.sdevprem.basictexteditor.common.animateUp
import com.sdevprem.basictexteditor.databinding.FragmentEditorBinding
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.RelativeFontSizeStyle
import com.sdevprem.basictexteditor.ui.editor.util.SimpleStyle
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorFragment : Fragment() {
    private var _binding: FragmentEditorBinding? = null
    private val binding: FragmentEditorBinding
        get() = _binding!!

    private val viewModel by viewModels<EditorViewModel>()
    private val args by navArgs<EditorFragmentArgs>()


    private val imgPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                with(binding.etEditor) {
                    val newS = viewModel.insertImage(it, text, selectionStart, selectionEnd)
                    val cursorPos = selectionEnd + (newS.length - text.length)
                    setText(newS, TextView.BufferType.SPANNABLE)
                    setSelection(cursorPos)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEditorBinding.inflate(
            inflater,
            container,
            false
        ).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editorAction.collect {
                    when (it) {
                        is EditorViewAction.ShowNote -> {
                            etEditor.setText(it.noteWithStyle.body)
                            etTitle.setText(it.noteWithStyle.title)
                            applyRange(it.noteWithStyle.ranges)
                        }

                        EditorViewAction.NavigateBack -> {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }

        fragment = this@EditorFragment

        editorToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        editorToolbar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.menu_save -> {
                    viewModel.saveNote(etTitle.text.toString(), etEditor.text.toString())
                    true
                }

                R.id.menu_delete -> {
                    viewModel.deleteNote()
                    true
                }

                else -> false
            }
        }

        btnBold.setOnClickListener {
            toggleFormatting(BoldStyle)
        }
        btnItalic.setOnClickListener {
            toggleFormatting(ItalicStyle)
        }
        btnUnderline.setOnClickListener {
            toggleFormatting(UnderLineStyle)
        }
        btnFontSize.setOnClickListener {
            fontSizeContainer.animateUp()
        }
        btnChangeFont.setOnClickListener {
            fontTypeContainer.animateUp()
        }
        btnRemoveFont.setOnClickListener {
            viewModel.removeFont(
                etEditor.text,
                etEditor.selectionStart,
                etEditor.selectionEnd
            )
        }

        btnInsertImg.setOnClickListener {
            imgPickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        etEditor.doOnTextChanged { _, start, before, count ->
            viewModel.updateRanges(start, count, before)
        }

        if (args.id < 0)
            editorToolbar.menu.findItem(R.id.menu_delete).isVisible = false


        return@with

    }

    fun applyFont(viewId: Int) = with(binding.etEditor) {
        when (viewId) {
            R.id.btn_nunito -> {
                viewModel.applyFont(
                    selectionStart,
                    selectionEnd,
                    text,
                    NoteUtils.FONT_NUNITO
                )
            }

            R.id.btn_robotSlab -> {
                viewModel.applyFont(
                    selectionStart,
                    selectionEnd,
                    text,
                    NoteUtils.FONT_ROBOTO_SLAB
                )
            }
        }
    }

    fun increaseFontSize(sizeMultiplier: Float) = with(binding) {
        closeSelector(fontSizeContainer.id)
        viewModel.toggleFormatting(
            RelativeFontSizeStyle(sizeMultiplier),
            etEditor.text,
            etEditor.selectionStart,
            etEditor.selectionEnd,
        )
    }


    fun closeSelector(
        viewId: Int
    ) = when (viewId) {
        R.id.font_size_container -> binding.fontSizeContainer.animateDown()
        R.id.font_type_container -> binding.fontTypeContainer.animateDown()
        else -> {

        }
    }


    private fun toggleFormatting(style: SimpleStyle) = with(binding) {
        viewModel.toggleFormatting(
            style,
            etEditor.editableText,
            etEditor.selectionStart,
            etEditor.selectionEnd
        )
    }

    private fun applyRange(range: List<SpanStyleRange>) = with(binding.etEditor.text) {
        range.forEach {
            if (it.start < it.end)
                setSpan(it.format, it.start, it.end, it.style.spannableFlag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}