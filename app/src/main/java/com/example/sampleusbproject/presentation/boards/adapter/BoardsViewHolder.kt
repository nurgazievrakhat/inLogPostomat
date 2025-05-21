package com.example.sampleusbproject.presentation.boards.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.databinding.ItemBoardsBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent

class BoardsViewHolder(
    private val binding: ItemBoardsBinding
) : ViewHolder(binding.root) {

    init {
        val layoutManager = SafeFlexboxLayoutManager(binding.root.context)
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        binding.rvBoard.layoutManager = layoutManager
        binding.rvBoard.setHasFixedSize(true)
        binding.rvBoard.isNestedScrollingEnabled = false
    }

    fun onBind(adapter: BoardAdapter) {
        binding.rvBoard.adapter = adapter
    }

}