package com.example.sampleusbproject.presentation.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSuccessBinding
import com.example.sampleusbproject.presentation.numberPad.PackageType
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.visible

class SuccessFragment : Fragment(R.layout.fragment_success) {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
//    private val args: SuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = PackageType.getType(arguments?.getInt("type") ?: 0)
        binding.btnToMain.setOnClickListener {
            findNavController().navigate(
                R.id.mainFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}