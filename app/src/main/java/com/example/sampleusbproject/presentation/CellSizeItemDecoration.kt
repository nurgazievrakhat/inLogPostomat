package com.example.sampleusbproject.presentation

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.utils.toPx

class CellSizeItemDecoration : RecyclerView.ItemDecoration() {
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
        val adapterPosition = parent.getChildAdapterPosition(view)
        val spaceAround = view.context.toPx(28)

        //first element
        if (adapterPosition != 0) {
            outRect.top = spaceAround.toInt()
        }
    }
}