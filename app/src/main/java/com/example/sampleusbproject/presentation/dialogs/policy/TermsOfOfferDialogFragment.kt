package com.example.sampleusbproject.presentation.dialogs.policy

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.databinding.DialogTermsOfOfferBinding
import com.example.sampleusbproject.presentation.base.NonClosableDialogFragment

class TermsOfOfferDialogFragment:
    NonClosableDialogFragment<DialogTermsOfOfferBinding>(DialogTermsOfOfferBinding::inflate, width = 580, height = 704) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDesc.movementMethod = ScrollingMovementMethod()
        binding.btnAccept.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}