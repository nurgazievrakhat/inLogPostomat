package com.example.sampleusbproject.presentation.boards

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.databinding.ItemBoardBinding

class BoardViewHolder(
    private val binding: ItemBoardBinding
): ViewHolder(binding.root) {

    fun onBind(model: Board){
        binding.tvNumber.text = model.number.toString()
    }

}