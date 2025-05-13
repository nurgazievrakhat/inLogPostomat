package com.example.sampleusbproject.presentation.dialogs.anotherBoard

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.databinding.DialogAnotherBoardBinding
import com.example.sampleusbproject.presentation.base.NonClosableDialogFragment

class AnotherBoardDialogFragment :
    NonClosableDialogFragment<DialogAnotherBoardBinding>(DialogAnotherBoardBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}