package com.example.sampleusbproject.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.SystemClock
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.sampleusbproject.R

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

fun Fragment.getColorStateList(@ColorRes color: Int) =
    ColorStateList.valueOf(requireContext().getColor(color))

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Int.showWordWithDeclination(
    @StringRes singular: Int,
    @StringRes firstDeclination: Int,
    @StringRes secondDeclination: Int
): Int {
    val num100 = this % 100
    return if (num100 in 5..20) secondDeclination else
        when (num100 % 10) {
            1 -> singular
            in 2..4 -> firstDeclination
            else -> secondDeclination
        }
}

private var lastClickTime: Long = 0

fun TextView.setLinkedText(
    baseText: String,
    linkText: String,
    secondLinkText: String,
    textAfter: String,
    textEnd: String,
    onSecondLinkClick: (() -> Unit?)?,
    onLinkClick: () -> Unit
) {

    this.highlightColor = ContextCompat.getColor(context, R.color.transparent)

    val clickableSpan = object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.blue_baby, null)
            ds.isUnderlineText = false     // без подчеркивания
            ds.bgColor = Color.TRANSPARENT // на всякий случай фон тоже прозрачный
        }

        override fun onClick(widget: View) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime > 2000) { // allow clicks only once in 2 seconds
                lastClickTime = currentTime
                widget.cancelPendingInputEvents()
                onLinkClick()
            }
        }
    }

    val clickableSecondSpan = object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.blue, null)
            ds.isUnderlineText = false     // без подчеркивания
            ds.bgColor = Color.TRANSPARENT // на всякий случай фон тоже прозрачный
        }

        override fun onClick(widget: View) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime > 2000) { // allow clicks only once in 2 seconds
                lastClickTime = currentTime
                widget.cancelPendingInputEvents()
                onSecondLinkClick?.let { it() }
            }
        }
    }

    val linktext = SpannableString(linkText)
    val secondLinkedtext = SpannableString(secondLinkText)

    linktext.setSpan(clickableSpan, 0, linktext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    secondLinkedtext.setSpan(
        clickableSecondSpan,
        0,
        secondLinkedtext.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val cs =
        TextUtils.expandTemplate("$baseText ^1 $textAfter ^2 $textEnd", linktext, secondLinkedtext)

    this.text = cs
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun Fragment.makeToast(@StringRes message: Int){
    Toast.makeText(requireContext(), requireContext().getString(message), Toast.LENGTH_LONG).show()
}

fun Activity.makeToast(@StringRes message: Int){
    Toast.makeText(this, this.getString(message), Toast.LENGTH_LONG).show()
}