package com.example.sampleusbproject.presentation.qr

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentQrCodeBinding
import kg.finik.android.sdk.CreateItemHandlerWidget
import kg.finik.android.sdk.FinikActivity
import kg.finik.android.sdk.FinikSdkLocale
import kg.finik.android.sdk.PaymentMethod

class QrFragment : Fragment(R.layout.qr_fragment) {
    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!

    private val finikLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val paymentResultJson = data?.getStringExtra("paymentResultJson")
                Log.d("MainActivity", "Payment result: $paymentResultJson")
                // Обработка success или failure
            } else {
                Log.d("MainActivity", "Пользователь вышел из Finik по кнопке назад")
                // Обработка кнопки назад
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        launchFinik()
    }

    private fun setupListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun launchFinik() {
        val intent = Intent(requireContext(), FinikActivity::class.java).apply {
            putExtra("apiKey", "da2-g7s2jntzuzcojmj4fh37msznmq")
            putExtra(
                "widget",
                CreateItemHandlerWidget(
                    accountId = "e5574818-f448-420a-816e-aab6b1f1c26e",
                    name = "Test",
                    fixedAmount = 2300.0
                )
            )
            putExtra("paymentMethod", PaymentMethod.QR as Parcelable)
            putExtra("locale", FinikSdkLocale.RU as Parcelable)
            putExtra("isBeta", false)
        }

        finikLauncher.launch(intent)
    }
} 