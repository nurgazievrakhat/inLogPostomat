package com.example.sampleusbproject.utils.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import com.example.sampleusbproject.R

class QwertyKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var onKeyClickListener: ((String) -> Unit)? = null
    var onDeleteClickListener: (() -> Unit)? = null
    var onShiftClickListener: (() -> Unit)? = null
    var onModeChangeClickListener: (() -> Unit)? = null

    private var isUpperCase = true

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_qwerty_keyboard, this, true)
        setupKeys()
    }

    private fun setupKeys() {
        val keyIds = listOf(
            R.id.btnQ, R.id.btnW, R.id.btnE, R.id.btnR, R.id.btnT, R.id.btnY, R.id.btnU, R.id.btnI, R.id.btnO, R.id.btnP,
            R.id.btnA, R.id.btnS, R.id.btnD, R.id.btnF, R.id.btnG, R.id.btnH, R.id.btnJ, R.id.btnK, R.id.btnL,
            R.id.btnZ, R.id.btnX, R.id.btnC, R.id.btnV, R.id.btnB, R.id.btnN, R.id.btnM,
            R.id.btnAt, R.id.btnDot, R.id.btnSpace
        )
        keyIds.forEach { id ->
            findViewById<Button>(id)?.setOnClickListener {
                val text = (it as Button).text.toString()
                onKeyClickListener?.invoke(if (isUpperCase) text.uppercase() else text.lowercase())
            }
        }
        findViewById<Button>(R.id.btnDelete)?.setOnClickListener {
            onDeleteClickListener?.invoke()
        }
        findViewById<Button>(R.id.btnShift)?.setOnClickListener {
            isUpperCase = !isUpperCase
            updateCase()
            onShiftClickListener?.invoke()
        }
        findViewById<Button>(R.id.btn123)?.setOnClickListener {
            onModeChangeClickListener?.invoke()
        }
    }

    private fun updateCase() {
        val letterIds = listOf(
            R.id.btnQ, R.id.btnW, R.id.btnE, R.id.btnR, R.id.btnT, R.id.btnY, R.id.btnU, R.id.btnI, R.id.btnO, R.id.btnP,
            R.id.btnA, R.id.btnS, R.id.btnD, R.id.btnF, R.id.btnG, R.id.btnH, R.id.btnJ, R.id.btnK, R.id.btnL,
            R.id.btnZ, R.id.btnX, R.id.btnC, R.id.btnV, R.id.btnB, R.id.btnN, R.id.btnM
        )
        letterIds.forEach { id ->
            findViewById<Button>(id)?.text = if (isUpperCase)
                resources.getResourceEntryName(id).removePrefix("btn").uppercase()
            else
                resources.getResourceEntryName(id).removePrefix("btn").lowercase()
        }
    }
} 