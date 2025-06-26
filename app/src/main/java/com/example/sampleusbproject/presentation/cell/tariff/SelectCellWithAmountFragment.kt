package com.example.sampleusbproject.presentation.cell.tariff

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSelectCellWithAmountBinding
import com.example.sampleusbproject.presentation.CellSizeItemDecoration
import com.example.sampleusbproject.presentation.FinikPaymentModel
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.cell.CenterItemDecoration
import com.example.sampleusbproject.presentation.cell.SelectCellAdapter
import com.example.sampleusbproject.presentation.cell.SelectCellModel
import com.example.sampleusbproject.presentation.cell.SelectCellNewAdapter
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.commonViewModel.SelectedCell
import com.example.sampleusbproject.presentation.days.adapter.PayerDays
import com.example.sampleusbproject.presentation.days.adapter.SelectDay
import com.example.sampleusbproject.presentation.days.adapter.SelectDayAdapter
import com.example.sampleusbproject.presentation.days.adapter.getSelectList
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.makeToast
import com.example.sampleusbproject.utils.showWordWithDeclination
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kg.finik.android.sdk.CreateItemHandlerWidget
import kg.finik.android.sdk.FinikActivity
import kg.finik.android.sdk.FinikSdkLocale
import kg.finik.android.sdk.PaymentMethod
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCellWithAmountFragment :
    BaseViewModelFragment<SelectCellWithAmountViewModel, FragmentSelectCellWithAmountBinding>(
        R.layout.fragment_select_free_cell,
        SelectCellWithAmountViewModel::class.java,
        FragmentSelectCellWithAmountBinding::inflate
    ) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

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
                    if (model.status.lowercase() == "succeeded") {
                        val selected = adapter.currentList.find { it.isSelected }
                        val selectedDay = daysAdapter.currentList.find { it.isSelected }
                        val sumOfPay =
                            binding.tvAmount.text.toString().filter { it.isDigit() }.ifBlank { "0" }
                                .toLong()

                        viewModel.createTransaction(
                            sumOfPay,
                            selected?.cellId ?: "",
                            commonViewModel.phoneNumber,
                            commonViewModel.receiverPhoneNumber,
                            selectedDay?.day ?: -1
                        )

                    } else {
                        makeToast(R.string.text_something_went_wrong)
                    }
                } catch (e: Exception) {
                    makeToast(R.string.text_something_went_wrong)
                }
            } else {
                Log.d("MainActivity", "Пользователь вышел из Finik по кнопке назад")
            }
        }

    private var prevCellSelectedPos: Int = -1
    private var prevDaySelectedPos: Int = -1

    private lateinit var adapter: SelectCellNewAdapter

    private val daysAdapter: SelectDayAdapter by lazy {
        SelectDayAdapter(this::onDayClick)
    }

    private fun onDayClick(pos: Int, model: SelectDay) {
        if (prevDaySelectedPos == pos)
            return

        if (pos > -1) {
            daysTotal(daysAdapter.currentList[pos])
            onDaySelect(true, pos)
        }

        if (prevDaySelectedPos > -1) {
            onDaySelect(false, prevDaySelectedPos)
        }

        prevDaySelectedPos = pos
        updateSum()
    }

    private fun onDaySelect(isSelected: Boolean, pos: Int) {
        val list = daysAdapter.currentList.toMutableList()
        list[pos].isSelected = isSelected
        daysAdapter.notifyItemChanged(pos)
        check()
    }

    private fun onClick(pos: Int, model: SelectCellModel) {
        if (prevCellSelectedPos == pos)
            return

        if (prevCellSelectedPos > -1) {
            select(false, prevCellSelectedPos)
        }

        cellTotal(adapter.currentList[pos])
        select(true, pos)
        prevCellSelectedPos = pos
        updateSum()
    }

    private fun select(isSelected: Boolean, pos: Int) {
        val list = adapter.currentList.toMutableList()
        list[pos].isSelected = isSelected
        adapter.notifyItemChanged(pos)
        check()
    }

    private fun cellTotal(model: SelectCellModel) {
        val size = String.format(
            binding.root.context.getString(R.string.text_cell_size_title),
            model.boardSize.name
        )
        binding.itemTotalCell.setContent(size)
        val amount = String.format(
            binding.root.context.getString(R.string.text_cell_amount),
            model.cellPrice.toString()
        )
        binding.itemTotalAmount.setContent(amount)
    }

    private fun daysTotal(model: SelectDay) {
        val daysTitle = "${model.day} ${
            binding.root.context.getString(
                model.day.showWordWithDeclination(
                    R.string.text_day_1,
                    R.string.text_day_2_4,
                    R.string.text_day_multiple
                )
            )
        }"
        binding.itemTotalBestBefore.setContent(daysTitle)
    }

    private fun updateSum() {
        val selectedDay = daysAdapter.currentList.find { it.isSelected }
        val selectedCell = adapter.currentList.find { it.isSelected }
        if (selectedDay != null && selectedCell != null) {
            binding.tvAmount.text = "${selectedDay.day * selectedCell.cellPrice} сом"
        }
    }

    override fun initialize() {
        adapter = SelectCellNewAdapter(this::onClick, true)
        viewModel.getFreCells(commonViewModel.selectedCell?.size)
        binding.rvCells.adapter = adapter
        binding.rvCells.addItemDecoration(CellSizeItemDecoration())
        binding.rvDays.adapter = daysAdapter
        daysAdapter.submitList(commonViewModel.payer!!.getSelectList(commonViewModel.days))
        if (commonViewModel.payer!! == PayerDays.SENDER) {
            binding.tvToPay.text = requireContext().getString(R.string.text_to_pay)
        } else {
            binding.tvToPay.text = requireContext().getString(R.string.text_sum)
        }
        if (commonViewModel.orderId.isNotBlank())
            binding.btnBack.gone()
    }

    override fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            val selected = adapter.currentList.find { it.isSelected }
            val selectedDay = daysAdapter.currentList.find { it.isSelected }
            val sumOfPay =
                binding.tvAmount.text.toString().filter { it.isDigit() }.ifBlank { "0" }.toLong()

            if (selected == null) {
                makeToast(R.string.text_choose_size_error)
                return@setOnClickListener
            }

            if (selectedDay == null) {
                makeToast(R.string.text_choose_day_error)
                return@setOnClickListener
            }

            if (commonViewModel.orderId.isBlank()) {
                if (commonViewModel.payer!! == PayerDays.SENDER)
                    launchFinik(sumOfPay)
                else
                    viewModel.createOrder(
                        selected.cellId ?: "",
                        commonViewModel.phoneNumber,
                        commonViewModel.receiverPhoneNumber,
                        selectedDay.day
                    )
            } else
                viewModel.updateCell(
                    selected.cellId ?: "",
                    commonViewModel.orderId,
                    selectedDay.day
                )
        }
    }

    private fun check() {
        val selected = adapter.currentList.find { it.isSelected }
        val selectedDay = daysAdapter.currentList.find { it.isSelected }

        binding.btnContinue.isEnabled = selected != null && selectedDay != null
    }

    override fun setupSubscribers() {
        viewModel.createSuccessEvent.observe(viewLifecycleOwner) {
            commonViewModel.orderId = it
            saveData()
            findNavController().navigate(R.id.action_selectCellWithAmountFragment_to_leaveParcelOpenedBoardFragment)
        }
        viewModel.updateSuccessEvent.observe(viewLifecycleOwner) {
            saveData()
            findNavController().navigate(R.id.action_selectCellWithAmountFragment_to_leaveParcelOpenedBoardFragment)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            makeToast(R.string.text_something_went_wrong)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.freeCells.collect {
                    adapter.submitList(it)
                }
            }
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
            putExtra("paymentMethod", PaymentMethod.QR as Parcelable)
            putExtra("locale", FinikSdkLocale.RU as Parcelable)
            putExtra("isBeta", false)
        }

        finikLauncher.launch(intent)
    }

    fun saveData() {
        val selected = adapter.currentList.find { it.isSelected }
        val selectedDay = daysAdapter.currentList.find { it.isSelected }

        commonViewModel.days = selectedDay?.day ?: 0
        commonViewModel.selectedCell = SelectedCell(
            selected?.cellId ?: "",
            number = selected?.number ?: 0L,
            size = selected?.boardSize ?: BoardSize.S
        )
        val sumOfPay =
            binding.tvAmount.text.toString().filter { it.isDigit() }.ifBlank { "0" }.toInt()
        commonViewModel.sumOfPay = sumOfPay

        prevCellSelectedPos = -1
        prevDaySelectedPos = -1
    }

}