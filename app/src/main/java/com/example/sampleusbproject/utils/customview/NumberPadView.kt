package com.example.sampleusbproject.utils.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sampleusbproject.R
import com.google.android.material.button.MaterialButton

class NumberPadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_number_pad, this, true)
    }

    // Пример: доступ к кнопке
    fun setOnKeyClickListener(listener: (String) -> Unit) {
        val buttons = listOf(
            findViewById<MaterialButton>(R.id.btn_0),
            findViewById<MaterialButton>(R.id.btn_1),
            findViewById<MaterialButton>(R.id.btn_2),
            findViewById<MaterialButton>(R.id.btn_3),
            findViewById<MaterialButton>(R.id.btn_4),
            findViewById<MaterialButton>(R.id.btn_5),
            findViewById<MaterialButton>(R.id.btn_6),
            findViewById<MaterialButton>(R.id.btn_7),
            findViewById<MaterialButton>(R.id.btn_8),
            findViewById<MaterialButton>(R.id.btn_9)
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                listener(button.text.toString())
            }
        }
    }
}