package com.example.sampleusbproject.presentation.boards

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sampleusbproject.databinding.ItemBoardsBinding

class BoardsAdapter: ListAdapter<BoardsModel, BoardsViewHolder>(BoardsModelDiff()) {

    class BoardsModelDiff : DiffUtil.ItemCallback<BoardsModel>() {
        override fun areItemsTheSame(oldItem: BoardsModel, newItem: BoardsModel): Boolean {
            return oldItem.list.size == newItem.list.size
        }

        override fun areContentsTheSame(oldItem: BoardsModel, newItem: BoardsModel): Boolean {
            return oldItem.list.size == newItem.list.size
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardsViewHolder {
        val binding = ItemBoardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BoardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardsViewHolder, position: Int) {
        getItem(position)?.let {
            Log.e("asadasda", "onBindViewHolder: $position start", )
            holder.itemView.post {
                Log.e("asadasda", "onBindViewHolder: $position end", )
                val minItemHeight = holder.itemView.height / it.heightItemCount
                val minItemWidth = holder.itemView.width / it.widthItemCount
                holder.onBind(it.list, minItemHeight, minItemWidth)
            }
        }
    }

}