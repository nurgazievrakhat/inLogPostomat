package com.example.sampleusbproject.presentation.boards

import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.databinding.ItemBoardsBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent

class BoardsViewHolder(
    private val binding: ItemBoardsBinding
) : ViewHolder(binding.root) {

    fun onBind(data: List<Board>, minItemHeight: Int, minItemWidth: Int) {
        val adapter = BoardAdapter(minItemHeight, minItemWidth)
        binding.rvBoard.setHasFixedSize(true)
        binding.rvBoard.isNestedScrollingEnabled = false
        binding.rvBoard.adapter = adapter
        val layoutManager = SafeFlexboxLayoutManager(binding.root.context)
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        binding.rvBoard.layoutManager = layoutManager
        adapter.submitList(data)
    }

}