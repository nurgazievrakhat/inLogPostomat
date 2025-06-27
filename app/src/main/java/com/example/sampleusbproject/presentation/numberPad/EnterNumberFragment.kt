package com.example.sampleusbproject.presentation.numberPad

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentEnterNumberBinding
import com.example.sampleusbproject.domain.models.GetOrderError
import com.example.sampleusbproject.presentation.FinikPaymentModel
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.makeToast
import com.example.sampleusbproject.utils.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kg.finik.android.sdk.CreateItemHandlerWidget
import kg.finik.android.sdk.FinikActivity
import kg.finik.android.sdk.FinikSdkLocale
import kg.finik.android.sdk.PaymentMethod

@AndroidEntryPoint
class EnterNumberFragment :
    BaseViewModelFragment<EnterNumberViewModel, FragmentEnterNumberBinding>(
        R.layout.fragment_enter_number,
        EnterNumberViewModel::class.java,
        FragmentEnterNumberBinding::inflate
    ) {

    private val finikLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val paymentResultJson = data?.getStringExtra("paymentResultJson")
                Log.d("MainActivity", "Payment result: $paymentResultJson")
                try {
                    val model = Gson().fromJson<FinikPaymentModel>(
                        paymentResultJson,
                        FinikPaymentModel::class.java
                    )
                    val successModel = viewModel.paymentEvent.value
                    if (model.status.lowercase() == "succeeded" && successModel != null) {
                        viewModel.payed(successModel.second, successModel.first)
                    } else {
                        makeToast(R.string.text_something_went_wrong)
                    }
                } catch (e: Exception){
                    makeToast(R.string.text_something_went_wrong)
                }
            } else {
                Log.d("MainActivity", "Пользователь вышел из Finik по кнопке назад")
            }
        }

    override fun initialize() {
        binding.etCodeInput.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
    }

    override fun setupListeners() {
        binding.keypadGrid.setOnKeyClickListener {
            binding.etCodeInput.text.append(it)
            if (binding.tvError.isVisible)
                binding.tvError.gone()
        }
        binding.btnClear.setOnClickListener {
            if (binding.etCodeInput.text.isNotEmpty())
                binding.etCodeInput.text.delete(
                    binding.etCodeInput.text.length - 1,
                    binding.etCodeInput.text.length
                )
            if (binding.tvError.isVisible)
                binding.tvError.gone()
        }
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            if (code.length < 4)
                binding.tvError.visible()
            else
                viewModel.getOrderByPassword(code)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setupSubscribers() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            if (it is GetOrderError.Unexpected)
                makeToast(R.string.text_something_went_wrong)
            else
                binding.tvError.visible()
        }
        viewModel.successEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.openedBoardFragment, bundleOf("cell" to it))
        }
        viewModel.paymentEvent.observe(viewLifecycleOwner) {
            launchFinik(it.first)
        }
    }

    fun launchFinik(sum: Long) {
        val intent = Intent(requireContext(), FinikActivity::class.java).apply {
            putExtra("apiKey", "da2-g7s2jntzuzcojmj4fh37msznmq")
            putExtra(
                "widget",
                CreateItemHandlerWidget(
                    accountId = "e5574818-f448-420a-816e-aab6b1f1c26e",
                    name = "Test",
                    fixedAmount = sum.toDouble()
                )
            )
            putExtra("paymentMethods", arrayOf(PaymentMethod.QR))
            putExtra("enableShare", false)
            putExtra("enableAnimation", false)
            putExtra("tapableSupportButtons", false)
            putExtra("locale", FinikSdkLocale.RU as Parcelable)
            putExtra("isBeta", false)
        }
        Log.d("FINIK", "Flags = 0x${intent.flags.toString(16)}")
        finikLauncher.launch(intent)
    }

}