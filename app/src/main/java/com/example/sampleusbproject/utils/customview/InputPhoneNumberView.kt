package com.example.sampleusbproject.utils.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.LayoutInputPhoneNumberBinding
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.visible
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class InputPhoneNumberView : ConstraintLayout {

    private lateinit var phoneNumberFormatWatcher: FormatWatcher

    private val binding: LayoutInputPhoneNumberBinding

    private var onEndListener: (() -> Unit)? = null

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
        inflater.inflate(R.layout.layout_input_phone_number, this, true)
        binding = LayoutInputPhoneNumberBinding.bind(this)

        setUpViews()
        setUpListeners()
    }

    private fun showError() {
        binding.etPhoneNumber.setTextColor(ResourcesCompat.getColor(resources, R.color.red, null))
        binding.tvCodeHint.setTextColor(ResourcesCompat.getColor(resources, R.color.red, null))
        binding.containerPhone.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.bg_error_input_phone,
            context.theme
        )
    }

    fun hideError() {
        binding.etPhoneNumber.setTextColor(
            ResourcesCompat.getColor(resources, R.color.black, null)
        )
        binding.containerPhone.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.bg_input_phone,
            context.theme
        )
        if (binding.etPhoneNumber.text?.isNotEmpty() == true) {
            binding.tvCodeHint.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.black_30, null
                )
            )
        } else {
            binding.tvCodeHint.setTextColor(binding.etPhoneNumber.hintTextColors)
        }
    }

    private fun setUpListeners() {
        binding.etPhoneNumber.addTextChangedListener {
            if (it.toString().isBlank())
                binding.etPhoneNumber.gone()
            else {
                binding.etPhoneNumber.visible()
                binding.etPhoneNumber.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.black, null)
                )
                binding.containerPhone.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_input_phone,
                    context.theme
                )

                val inputText = it.toString()

                if (inputText.isNotEmpty()) {
                    binding.tvCodeHint.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.black_30, null
                        )
                    )
                } else {
                    binding.tvCodeHint.setTextColor(binding.etPhoneNumber.hintTextColors)
                }

                if (inputText.length > 11)
                    onEndListener?.invoke()
            }
        }
    }

    fun onEndListener(listener: () -> Unit) {
        onEndListener = listener
    }

    fun clearCustomFocus() {
        binding.etPhoneNumber.clearFocus()
    }

    fun input(s: String){
        binding.etPhoneNumber.text?.append(s)
    }

    fun clear(){
        if (!binding.etPhoneNumber.text.isNullOrEmpty())
            binding.etPhoneNumber.text!!.delete(
                binding.etPhoneNumber.text!!.length - 1,
                binding.etPhoneNumber.text!!.length
            )
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