package com.example.sampleusbproject.presentation.cell

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.utils.toPx
import kotlin.math.max


class CenterItemDecoration() : RecyclerView.ItemDecoration() {
    /**
     *
     * [.startPadding] and [.endPadding] are final and required on initialization
     * because  [android.support.v7.widget.RecyclerView.ItemDecoration] are drawn
     * before the adapter's child views so you cannot rely on the child view measurements
     * to determine padding as the two are connascent
     *
     * see {@see [](https://en.wikipedia.org/wiki/Connascence_(computer_programming))
     */

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val totalWidth = parent.width

        Log.e("sdfsdfd", "totalWidth: $totalWidth")

        val itemWidth = view.context.toPx(217) // static width in layout
        Log.e("sdfsdfd", "viewWidth: ${itemWidth}")

        val itemSpace = view.context.toPx(12)
        val itemCount = parent.adapter!!.itemCount
        val itemsWidth = itemWidth * itemCount
        val contentWidth = itemsWidth + (itemSpace * (itemCount - 1))
        val adapterPosition = parent.getChildAdapterPosition(view)

        if (totalWidth > contentWidth) {
            val spaceAround = (totalWidth - contentWidth) / 2

            //first element
            if (adapterPosition == 0) {
                outRect.left = spaceAround.toInt()
            }

            if (adapterPosition == parent.adapter!!.itemCount - 1 &&
                parent.adapter!!.itemCount > 1
            ) {
                outRect.right = spaceAround.toInt()
            }
        }

        if (adapterPosition != 0)
            outRect.left = itemSpace.toInt()
    }
}