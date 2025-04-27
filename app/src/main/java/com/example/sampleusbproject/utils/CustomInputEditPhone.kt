package com.example.sampleusbproject.utils

import android.content.Context
import android.graphics.PorterDuff
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.LayoutCustomInputPhoneBinding
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class CustomInputEditPhone : ConstraintLayout {

    private lateinit var phoneNumberFormatWatcher: FormatWatcher

    private val binding: LayoutCustomInputPhoneBinding

    private var onEndListener: (() -> Unit)? = null

    var showError: String = ""
        set(value) {
            showError(value)
            field = value
        }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.layout_custom_input_phone, this, true)
        binding = LayoutCustomInputPhoneBinding.bind(this)

        setUpViews()
        setUpListeners()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed(500L) {
            if (binding.root.isVisible) {
                binding.etPhoneNumber.requestFocus()
                binding.etPhoneNumber.showKeyboard()
            }
        }
    }
    private fun showError(errorString: String) {
        if (binding.tvError.isVisible) {
            binding.tvError.text = errorString
        } else {
            binding.tvError.visible()
            binding.tvError.text = errorString
            binding.etPhoneNumber.setTextColor(ResourcesCompat.getColor(resources,R.color.red, null))
            binding.etPhoneNumber.setHintTextColor(ResourcesCompat.getColor(resources,R.color.red, null))
            binding.tvCodeHint.setTextColor(ResourcesCompat.getColor(resources,R.color.red, null))
            binding.divider.setColorFilter(
                ResourcesCompat.getColor(resources,R.color.red, null),
                PorterDuff.Mode.SRC_IN
            )
            binding.containerPhone.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.bg_error_enter_number,
                context.theme
            )
        }
    }

    fun hideError() {
        binding.tvError.gone()
        binding.etPhoneNumber.setHintTextColor(ResourcesCompat.getColor(resources,R.color.gray, null))
        binding.etPhoneNumber.setTextColor(
            ResourcesCompat.getColor(resources,R.color.dark_question_bg, null)
        )
        binding.divider.setColorFilter(
            ResourcesCompat.getColor(resources,R.color.gray, null),
            PorterDuff.Mode.SRC_IN
        )
        binding.containerPhone.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.bg_enter_number,
            context.theme
        )
        if (binding.etPhoneNumber.text?.isNotEmpty() == true) {
            binding.tvCodeHint.setTextColor(ResourcesCompat.getColor(resources,R.color.dark_question_bg, null))
        } else {
            binding.tvCodeHint.setTextColor(binding.etPhoneNumber.hintTextColors)
        }
    }
    private fun setUpListeners() {
        binding.etPhoneNumber.addTextChangedListener {
            binding.tvError.gone()
            binding.etPhoneNumber.setHintTextColor(ResourcesCompat.getColor(resources,R.color.gray, null))
            binding.etPhoneNumber.setTextColor(
                ResourcesCompat.getColor(resources,R.color.dark_question_bg, null)
            )
            binding.divider.setColorFilter(
                ResourcesCompat.getColor(resources,R.color.gray, null),
                PorterDuff.Mode.SRC_IN
            )
            binding.containerPhone.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.bg_enter_number,
                context.theme
            )

            val inputText = it.toString()

            if (inputText.isNotEmpty()) {
                binding.tvCodeHint.setTextColor(ResourcesCompat.getColor(resources,R.color.dark_question_bg, null))
            } else {
                binding.tvCodeHint.setTextColor(binding.etPhoneNumber.hintTextColors)
            }

            if (inputText.length > 11)
                onEndListener?.invoke()
        }
    }

    fun onEndListener(listener: () -> Unit) {
        onEndListener = listener
    }

    fun showCustomKeyboard() {
//        binding.etPhoneNumber.requestFocus()
//        binding.etPhoneNumber.showKeyboard()
    }

    fun clearCustomFocus() {
        binding.etPhoneNumber.clearFocus()
    }

    private fun setUpViews() {
        binding.etPhoneNumber.inputType = InputType.TYPE_CLASS_PHONE

        val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("___ __ __ __")
        val inputMask = MaskImpl.createTerminated(slots).apply {
            isHideHardcodedHead = true
        }
        phoneNumberFormatWatcher = MaskFormatWatcher(inputMask)
        phoneNumberFormatWatcher.installOn(binding.etPhoneNumber)
    }

    fun getRawPhoneNumber() = "+996${phoneNumberFormatWatcher.mask.toUnformattedString()}"
}