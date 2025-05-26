package com.example.sampleusbproject.presentation.days.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.ItemDayChooseBinding

class SelectDayViewHolder(
    val binding: ItemDayChooseBinding,
    private val onClick: (Int, SelectDay) -> Unit
) : ViewHolder(binding.root) {

    fun onBind(model: SelectDay) {
        if (model.isAvailableToSelect)
            binding.root.alpha = 1.0f
        else
            binding.root.alpha = 0.4f

        binding.tvDay.text = model.day.toString()
        binding.root.setCardBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                if (model.isSelected) R.color.blue_baby else R.color.transparent
            )
        )
        binding.root.strokeWidth = if (model.isSelected) 0 else 1
        binding.tvDay.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    if (model.isSelected) R.color.white else R.color.text_secondary
                )
            )
        )
        binding.root.setOnClickListener {
            if (adapterPosition != -1 && model.isAvailableToSelect)
                onClick(adapterPosition, model)
        }
    }

}