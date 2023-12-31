package com.sdevprem.basictexteditor.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sdevprem.basictexteditor.data.model.Note
import com.sdevprem.basictexteditor.databinding.NoteItemBinding


class NoteAdapter(
    private val onNoteClicked: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(ComparatorDiffUtil()) {

    class NoteViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = getItem(position).let {
        with(holder.binding) {
            this.note = it
            root.setOnClickListener { _ -> onNoteClicked(it) }
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}