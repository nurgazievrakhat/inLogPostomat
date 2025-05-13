package com.example.sampleusbproject.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

internal fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun DialogFragment.setSize(width: Int, height: Int) {
    val widthPx = requireContext().toPx(width)
    val heightPx = requireContext().toPx(height)
    dialog?.window?.setLayout(widthPx.toInt(), heightPx.toInt())
}

fun Context.toPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
)
