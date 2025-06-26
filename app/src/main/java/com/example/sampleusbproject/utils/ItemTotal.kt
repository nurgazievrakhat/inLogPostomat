package com.example.sampleusbproject.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.ViewTotalItemBinding

class ItemTotal : ConstraintLayout {

    private val binding: ViewTotalItemBinding

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_total_item, this, true)
        binding = ViewTotalItemBinding.bind(this)
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    fun setContent(text: String){
        binding.tvContent.text = text
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.ItemTotal, 0, 0)
            try {
                val title = attributes.getResourceId(R.styleable.ItemTotal_total_title, -1)
                Log.e("dsfsdfsd", "init: $title")
                if (title > -1)
                    binding.tvTitle.text = context.getString(title)
            } catch (e: Exception){
                Log.e("dsfsdfsd", "init: ${e.message}")
            } finally {
                attributes.recycle()
            }
        }
    }

}