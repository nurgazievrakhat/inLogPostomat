package com.example.sampleusbproject.presentation.cell

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sampleusbproject.databinding.LayoutSelectCellSizeNewBinding

class SelectCellNewAdapter(
    val onClick: (Int, SelectCellModel) -> Unit,
    val withAmount: Boolean
): ListAdapter<SelectCellModel, SelectCellNewViewHolder>(SelectCellDiff()) {

    class SelectCellDiff: DiffUtil.ItemCallback<SelectCellModel>() {
        override fun areItemsTheSame(oldItem: SelectCellModel, newItem: SelectCellModel): Boolean {
            return oldItem.cellId == newItem.cellId
        }

        override fun areContentsTheSame(
            oldItem: SelectCellModel,
            newItem: SelectCellModel
        ): Boolean {
            return oldItem.isSelected == newItem.isSelected && oldItem.isAvailableToChoose == newItem.isAvailableToChoose
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCellNewViewHolder {
        val binding = LayoutSelectCellSizeNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCellNewViewHolder(binding, onClick, withAmount)
    }

    override fun onBindViewHolder(holder: SelectCellNewViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

}