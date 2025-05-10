package com.example.sampleusbproject.utils.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import com.example.sampleusbproject.R

class CustomKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var targetEditText: EditText? = null
    private val deleteButton: Button
    private val keyboardLayout: GridLayout

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.custom_keyboard_view, this, true)
        keyboardLayout = findViewById(R.id.keyboard)
        deleteButton = findViewById(R.id.deleteButton)

        for (i in 0 until keyboardLayout.childCount) {
            val key = keyboardLayout.getChildAt(i)
            if (key is Button && key.id != R.id.deleteButton) {
                key.setOnClickListener {
                    targetEditText?.append(key.text)
                }
            }
        }

        deleteButton.setOnClickListener {
            targetEditText?.let {
                val text = it.text
                if (text.isNotEmpty()) {
                    it.setText(text.dropLast(1))
                    it.setSelection(it.text.length)
                }
            }
        }
    }

    fun bindTo(editText: EditText) {
        targetEditText = editText
        editText.showSoftInputOnFocus = false
    }
} 