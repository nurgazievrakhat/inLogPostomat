package com.example.sampleusbproject.presentation.boards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.databinding.ItemBoardBinding

class BoardAdapter(
    private val minItemHeight: Int,
    private val minItemWidth: Int,
    private val selectedNumber: Long
) : ListAdapter<CellSchema, BoardViewHolder>(BoardDiffUtil()) {

    class BoardDiffUtil : DiffUtil.ItemCallback<CellSchema>() {
        override fun areItemsTheSame(oldItem: CellSchema, newItem: CellSchema): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: CellSchema, newItem: CellSchema): Boolean {
            return oldItem.usable == newItem.usable
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val type = BoardSize.getType(viewType)
        binding.root.layoutParams = RecyclerView.LayoutParams(type.ratioWidth * minItemWidth, type.ratioHeight * minItemHeight)
        return BoardViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            BoardSize.getIntType(it.size)
        } ?: 0
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it, selectedNumber)
        }
    }

}