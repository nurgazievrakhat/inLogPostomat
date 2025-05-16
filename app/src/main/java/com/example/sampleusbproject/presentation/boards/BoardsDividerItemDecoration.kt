package com.example.sampleusbproject.presentation.boards

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.utils.dpToPx

class BoardsDividerItemDecoration(
    context: Context,
    private val marginStartDp: Int,
    private val marginEndDp: Int,
    private val orientation: Int,
    @DrawableRes drawable : Int
) : RecyclerView.ItemDecoration() {

    private val divider: Drawable? = ContextCompat.getDrawable(context, drawable)
    private val marginStartPx = marginStartDp.dpToPx(context)
    private val marginEndPx = marginEndDp.dpToPx(context)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider ?: return

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            if (orientation == RecyclerView.VERTICAL) {
                val left = parent.paddingLeft + marginStartPx
                val right = parent.width - parent.paddingRight - marginEndPx
                val top = child.bottom + params.bottomMargin
                val bottom = top + divider.intrinsicHeight
                divider.setBounds(left, top, right, bottom)
            } else {
                val top = parent.paddingTop
                val bottom = parent.height - parent.paddingBottom
                val left = child.right + params.rightMargin + marginStartPx
                val right = left + divider.intrinsicWidth
                divider.setBounds(left, top, right, bottom)
            }

            divider.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position % 2 != 0) {
            outRect.left = (divider?.intrinsicWidth ?: 0) + marginEndPx + marginStartPx
        }
    }
}