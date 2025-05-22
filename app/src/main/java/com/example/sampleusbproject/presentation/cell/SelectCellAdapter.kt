package com.example.sampleusbproject.presentation.cell

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sampleusbproject.databinding.LayoutSelectCellSizeBinding

class SelectCellAdapter(
    val onClick: (Int, SelectCellModel) -> Unit
): ListAdapter<SelectCellModel, SelectCellViewHolder>(SelectCellDiff()) {

    class SelectCellDiff: DiffUtil.ItemCallback<SelectCellModel>() {
        override fun areItemsTheSame(oldItem: SelectCellModel, newItem: SelectCellModel): Boolean {
            return oldItem.cellId == newItem.cellId
        }

        override fun areContentsTheSame(
            oldItem: SelectCellModel,
            newItem: SelectCellModel
        ): Boolean {
            return oldItem.isSelected == newItem.isSelected
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCellViewHolder {
        val binding = LayoutSelectCellSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCellViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: SelectCellViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

}