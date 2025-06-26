package com.example.sampleusbproject.presentation.boards.adapter

import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.ItemBoardBinding
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.visible

class BoardViewHolder(
    private val binding: ItemBoardBinding
) : ViewHolder(binding.root) {

    fun onBind(model: CellSchema, selectedNumber: Long) {
        if (model.usable) {
            binding.tvNumber.visible()
            binding.tvNumber.setTextColor(
                ColorStateList.valueOf(
                    binding.tvNumber.context.getColor(
                        if (model.number != selectedNumber)
                            R.color.shadow_black
                        else
                            R.color.white
                    )
                )
            )
            binding.tvNumber.text = model.number.toString()
            binding.mcvNumber.backgroundTintList = ColorStateList.valueOf(
                binding.mcvNumber.context.getColor(
                    if (model.number != selectedNumber)
                        R.color.transparent
                    else
                        R.color.blue_baby
                )
            )
        } else {
            binding.tvNumber.gone()
            binding.mcvNumber.backgroundTintList = ColorStateList.valueOf(
                binding.mcvNumber.context.getColor(
                    R.color.grey
                )
            )
        }
    }

}