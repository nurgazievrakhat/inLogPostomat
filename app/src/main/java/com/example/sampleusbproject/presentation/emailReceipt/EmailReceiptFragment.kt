package com.example.sampleusbproject.presentation.emailReceipt

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sampleusbproject.databinding.FragmentEmailReceiptBinding

class EmailReceiptFragment : Fragment() {

    private var _binding: FragmentEmailReceiptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.emailInput.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.emailInput, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        binding.btnSend.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (isValidEmail(email)) {
                Toast.makeText(requireContext(), "Чек будет отправлен на $email", Toast.LENGTH_SHORT).show()
                // Навигация или возврат результата
            } else {
                binding.emailInput.error = "Введите корректный email"
            }
        }

        binding.btnNoReceipt.setOnClickListener {
            Toast.makeText(requireContext(), "Чек не нужен", Toast.LENGTH_SHORT).show()
            // Навигация назад или другое действие
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}