package com.example.sampleusbproject.presentation.days.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sampleusbproject.databinding.ItemDayChooseBinding

class SelectDayAdapter(
    private val onClick: (Int, SelectDay) -> Unit
): ListAdapter<SelectDay, SelectDayViewHolder>(SelectDayDiff()) {

    class SelectDayDiff: DiffUtil.ItemCallback<SelectDay>() {
        override fun areItemsTheSame(oldItem: SelectDay, newItem: SelectDay): Boolean {
            Log.e("sdfsdf", "areItemsTheSame: $oldItem $newItem", )
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(oldItem: SelectDay, newItem: SelectDay): Boolean {
            Log.e("sdfsdf", "areContentsTheSame: $oldItem $newItem", )
            return (oldItem.isSelected == newItem.isSelected)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectDayViewHolder {
        val binding = ItemDayChooseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectDayViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: SelectDayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

}