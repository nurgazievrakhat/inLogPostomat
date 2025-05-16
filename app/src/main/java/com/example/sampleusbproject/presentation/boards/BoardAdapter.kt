package com.example.sampleusbproject.presentation.boards

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.databinding.ItemBoardBinding

class BoardAdapter(
    private val minItemHeight: Int,
    private val minItemWidth: Int
) : ListAdapter<Board, BoardViewHolder>(BoardDiffUtil()) {

    class BoardDiffUtil : DiffUtil.ItemCallback<Board>() {
        override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean {
            return oldItem.usable == newItem.usable
        }

        override fun areContentsTheSame(oldItem: Board, newItem: Board): Boolean {
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
            holder.onBind(it)
        }
    }

}