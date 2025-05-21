package com.example.sampleusbproject.presentation.boards.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sampleusbproject.databinding.ItemBoardsBinding

class BoardsAdapter: ListAdapter<BoardsModel, BoardsViewHolder>(BoardsModelDiff()) {

    val adapterMap: HashMap<Int, BoardAdapter> = hashMapOf()

    class BoardsModelDiff : DiffUtil.ItemCallback<BoardsModel>() {
        override fun areItemsTheSame(oldItem: BoardsModel, newItem: BoardsModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BoardsModel, newItem: BoardsModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardsViewHolder {
        val binding = ItemBoardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BoardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardsViewHolder, position: Int) {
        getItem(position)?.let {
            Log.e("asadasda", "onBindViewHolder: $position start", )
            val currentAdapter = adapterMap[position]
            if (currentAdapter != null){
                currentAdapter.submitList(it.list)
                holder.onBind(currentAdapter)
            } else {
                holder.itemView.post {
                    Log.e("asadasda", "onBindViewHolder: $position end", )
                    val minItemHeight = holder.itemView.height / it.heightItemCount
                    val minItemWidth = holder.itemView.width / it.widthItemCount
                    val newAdapter = BoardAdapter(minItemHeight, minItemWidth)
                    newAdapter.submitList(it.list)
                    holder.onBind(newAdapter)
                    adapterMap[position] = newAdapter
                }
            }
        }
    }

}