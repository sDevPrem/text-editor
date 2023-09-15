package com.sdevprem.basictexteditor.ui.editor

import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sdevprem.basictexteditor.R
import com.sdevprem.basictexteditor.databinding.FragmentEditorBinding
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.Range
import com.sdevprem.basictexteditor.ui.editor.util.SimpleStyle
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorFragment : Fragment() {
    private var _binding: FragmentEditorBinding? = null
    private val binding: FragmentEditorBinding
        get() = _binding!!

    private val viewModel by viewModels<EditorViewModel>()

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

        boldMenu.setOnClickListener {
            toggleFormatting(BoldStyle)
        }
        italicMenu.setOnClickListener {
            toggleFormatting(ItalicStyle)
        }
        underlineMenu.setOnClickListener {
            toggleFormatting(UnderLineStyle)
        }

        etEditor.doOnTextChanged { _, start, before, count ->
            viewModel.updateRanges(start, count, before)
        }


        return@with

    }

    private fun toggleFormatting(style: SimpleStyle) = with(binding) {
        viewModel.toggleFormatting(
            style,
            etEditor.editableText,
            etEditor.selectionStart,
            etEditor.selectionEnd
        )
    }

    private fun applyRange(range: List<Range>) = with(binding.etEditor.text) {
        range.forEach {
            if (it.start < it.end)
                setSpan(it.format, it.start, it.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}