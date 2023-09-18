package com.sdevprem.basictexteditor.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sdevprem.basictexteditor.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    private val adapter: NoteAdapter = NoteAdapter {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToEditorFragment(it.id)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        ).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        addNote.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToEditorFragment()
            )
        }

        noteList.layoutManager = GridLayoutManager(requireContext(), 2)
        noteList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notes.collectLatest {
                    adapter.submitList(it)
                    noteList.isVisible = it.isNotEmpty()
                    emptyLayout.isVisible = it.isEmpty()
                }
            }
        }
        return@with
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}