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
import com.sdevprem.basictexteditor.domain.provider.FontProvider
import com.sdevprem.basictexteditor.ui.editor.util.BoldStyle
import com.sdevprem.basictexteditor.ui.editor.util.ItalicStyle
import com.sdevprem.basictexteditor.ui.editor.util.RelativeFontSizeStyle
import com.sdevprem.basictexteditor.ui.editor.util.SimpleStyle
import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange
import com.sdevprem.basictexteditor.ui.editor.util.UnderLineStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditorFragment : Fragment() {
    private var _binding: FragmentEditorBinding? = null
    private val binding: FragmentEditorBinding
        get() = _binding!!

    private val viewModel by viewModels<EditorViewModel>()
    private val args by navArgs<EditorFragmentArgs>()
    private var shouldUpdate = true

    @Inject
    lateinit var fontProvider: FontProvider

    private val imgPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                with(binding.etEditor) {
                    val newSpannable = viewModel.insertImage(it, text, selectionStart, selectionEnd)

                    //get the new cursor position according to
                    //the difference
                    val cursorPos = selectionEnd + (newSpannable.length - text.length)

                    shouldUpdate = false
                    setText(newSpannable, TextView.BufferType.SPANNABLE)
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
            it.fragment = this
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

        initLayout()

        return@with

    }

    private fun initLayout() = with(binding) {
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
            inFontSize.root.animateUp()
        }
        btnChangeFont.setOnClickListener {
            inFontName.root.animateUp()
        }
        inFontName.btnRemoveFont.setOnClickListener {
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
            if (shouldUpdate) {
                viewModel.updateRanges(start, count, before)
            } else {
                shouldUpdate = true
            }
        }

        if (args.id < 0)
            editorToolbar.menu.findItem(R.id.menu_delete).isVisible = false

        inFontName.btnNunito.typeface = fontProvider.getFont(NoteUtils.FONT_NUNITO)
        inFontName.btnRobotSlab.typeface = fontProvider.getFont(NoteUtils.FONT_ROBOTO_SLAB)
    }

    fun applyFont(viewId: Int) = with(binding.etEditor) {
        //called from the layout using data binding
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

    /**
     * Increase the font according to given [sizeMultiplier]
     */
    fun increaseFontSize(sizeMultiplier: Float) = with(binding) {
        //called from the layout using data binding
        closeSelector(inFontSize.root.id)
        viewModel.toggleFormatting(
            RelativeFontSizeStyle(sizeMultiplier),
            etEditor.text,
            etEditor.selectionStart,
            etEditor.selectionEnd,
        )
    }


    fun closeSelector(viewId: Int) = when (viewId) {
        //called from the layout using data binding
        binding.inFontSize.root.id -> binding.inFontSize.root.animateDown()
        binding.inFontName.root.id -> binding.inFontName.root.animateDown()
        else -> {}
    }


    private fun toggleFormatting(style: SimpleStyle) = with(binding) {
        viewModel.toggleFormatting(
            style,
            etEditor.editableText,
            etEditor.selectionStart,
            etEditor.selectionEnd
        )
    }

    //apply ranges to the editor editTextView
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