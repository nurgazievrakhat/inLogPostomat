package com.example.sampleusbproject.presentation.receiver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentReceiverBinding
import com.example.sampleusbproject.utils.getColorStateList
import com.google.android.material.button.MaterialButton

class ReceiverFragment : Fragment(R.layout.fragment_receiver) {

    private var _binding: FragmentReceiverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAnother.setOnClickListener {
            deselect(binding.btnSelf)
            selected(binding.btnAnother)
        }
        binding.btnSelf.setOnClickListener {
            deselect(binding.btnAnother)
            selected(binding.btnSelf)
        }
    }

    fun selected(btn: MaterialButton) {
        btn.strokeColor = getColorStateList(R.color.blue_baby)
        btn.backgroundTintList = getColorStateList(R.color.blue_hovered)
        btn.setTextColor(getColorStateList(R.color.white))
    }

    fun deselect(btn: MaterialButton) {
        btn.strokeColor = getColorStateList(R.color.black_10)
        btn.backgroundTintList = getColorStateList(R.color.blue_light)
        btn.setTextColor(getColorStateList(R.color.shadow_black))
    }

}