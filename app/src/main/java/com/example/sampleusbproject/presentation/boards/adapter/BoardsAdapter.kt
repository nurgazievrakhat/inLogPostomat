package com.example.sampleusbproject.presentation.boards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sampleusbproject.databinding.ItemBoardsBinding

class BoardsAdapter(
    val selectedNumber: Long
): ListAdapter<CellsModel, BoardsViewHolder>(BoardsModelDiff()) {

    val adapterMap: HashMap<Int, BoardAdapter> = hashMapOf()

    class BoardsModelDiff : DiffUtil.ItemCallback<CellsModel>() {
        override fun areItemsTheSame(oldItem: CellsModel, newItem: CellsModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CellsModel, newItem: CellsModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardsViewHolder {
        val binding = ItemBoardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BoardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardsViewHolder, position: Int) {
        getItem(position)?.let {
            val currentAdapter = adapterMap[position]
            if (currentAdapter != null){
                currentAdapter.submitList(it.list)
                holder.onBind(currentAdapter)
            } else {
                holder.itemView.post {
                    val minItemHeight = holder.itemView.height / it.columnItemCount
                    val minItemWidth = holder.itemView.width / it.rawItemCount
                    val newAdapter = BoardAdapter(minItemHeight, minItemWidth, selectedNumber)
                    newAdapter.submitList(it.list)
                    holder.onBind(newAdapter)
                    adapterMap[position] = newAdapter
                }
            }
        }
    }

}