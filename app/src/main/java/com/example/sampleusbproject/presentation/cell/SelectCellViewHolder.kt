package com.example.sampleusbproject.presentation.cell

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.LayoutSelectCellSizeBinding
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.visible

class SelectCellViewHolder(
    private val binding: LayoutSelectCellSizeBinding,
    val onClick: (Int, SelectCellModel) -> Unit,
    val withAmount: Boolean
) : ViewHolder(
    binding.root
) {

    fun onBind(model: SelectCellModel) {
        binding.tvCellState.isVisible = !model.isSelected
        binding.tvTitle.text = String.format(
            binding.root.context.getString(R.string.text_cell_size_title),
            model.boardSize.name
        )
        if (withAmount) {
            binding.tvAmount.text = String.format(
                binding.root.context.getString(R.string.text_cell_amount),
                model.cellPrice.toString()
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
            binding.tvCellState.text =
                binding.root.context.getString(R.string.text_not_contains_free_cell)
            binding.tvCellState.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.black_30
                    )
                )
            )
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
            binding.tvCellState.text =
                binding.root.context.getString(R.string.text_contains_free_cell)
            binding.tvCellState.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.green
                    )
                )
            )
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

        val imgRes = when (model.boardSize) {
            BoardSize.S -> R.drawable.icon_box_s
            BoardSize.M -> R.drawable.icon_box_m
            BoardSize.L -> R.drawable.icon_box_l
            BoardSize.XL -> R.drawable.icon_box_xl
        }

        binding.ivBox.setImageResource(imgRes)

        binding.root.setOnClickListener {
            if (adapterPosition != -1 && isClickable)
                onClick(adapterPosition, model)
        }
    }

}