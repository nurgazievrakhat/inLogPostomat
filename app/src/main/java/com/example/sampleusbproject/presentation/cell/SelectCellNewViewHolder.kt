package com.example.sampleusbproject.presentation.cell

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.LayoutSelectCellSizeNewBinding
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.visible

class SelectCellNewViewHolder(
    private val binding: LayoutSelectCellSizeNewBinding,
    val onClick: (Int, SelectCellModel) -> Unit,
    val withAmount: Boolean
) : ViewHolder(
    binding.root
) {

    fun onBind(model: SelectCellModel) {
        binding.tvTitle.text = String.format(
            binding.root.context.getString(R.string.text_cell_size_title),
            model.boardSize.name
        )
        if (withAmount) {
            binding.tvAmount.text = String.format(
                binding.root.context.getString(R.string.text_cell_amount),
                model.boardSize.amount.toString()
            )
            binding.tvAmount.visible()
        } else {
            binding.tvAmount.gone()
        }
        val isClickable = model.isAvailableToChoose && model.hasFreeCells()

        if (isClickable) {
            binding.container.alpha = 1f
        } else {
            binding.container.alpha = 0.4f
        }

        if (!model.hasFreeCells()) {
            binding.btnChoose.setImageResource(R.drawable.iv_select_cell_inactive)
            binding.tvCellState.visible()
            binding.root.strokeColor =
                ContextCompat.getColor(binding.root.context, R.color.black_10)
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.white
                )
            )
        } else {
            binding.btnChoose.setImageResource(if (model.isSelected) R.drawable.iv_select_cell_active else R.drawable.iv_select_cell_inactive)
            binding.tvCellState.gone()
            binding.root.strokeColor =
                ContextCompat.getColor(
                    binding.root.context,
                    if (model.isSelected) R.color.blue_baby else R.color.black_10
                )
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (model.isSelected) R.color.blue_light else R.color.white
                )
            )
        }

        binding.root.setOnClickListener {
            if (adapterPosition != -1 && isClickable)
                onClick(adapterPosition, model)
        }
    }

}