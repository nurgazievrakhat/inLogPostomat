package com.example.sampleusbproject.presentation.boards

import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.ItemBoardBinding

class BoardViewHolder(
    private val binding: ItemBoardBinding
) : ViewHolder(binding.root) {

    fun onBind(model: Board) {
        binding.tvNumber.text = model.number.toString()
        binding.mcvNumber.backgroundTintList = ColorStateList.valueOf(
            binding.mcvNumber.context.getColor(
                if (model.usable)
                    R.color.transparent
                else
                    R.color.blue_baby
            )
        )
    }

}