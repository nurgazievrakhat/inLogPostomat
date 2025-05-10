package com.example.sampleusbproject.presentation.numberPad

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sampleusbproject.databinding.FragmentEmailReceiptBinding

class EmailReceiptFragment : Fragment() {
    private var _binding: FragmentEmailReceiptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Привязка клавиатуры к полю email
        binding.customKeyboard.bindTo(binding.emailInput)

        binding.btnSend.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (isValidEmail(email)) {
                // TODO: отправить email для чека
                Toast.makeText(requireContext(), "Чек будет отправлен на $email", Toast.LENGTH_SHORT).show()
                // Навигация или возврат результата
            } else {
                binding.emailInput.error = "Введите корректный email"
            }
        }

        binding.btnNoReceipt.setOnClickListener {
            // TODO: обработка отказа от чека
            Toast.makeText(requireContext(), "Чек не нужен", Toast.LENGTH_SHORT).show()
            // Навигация или возврат результата
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