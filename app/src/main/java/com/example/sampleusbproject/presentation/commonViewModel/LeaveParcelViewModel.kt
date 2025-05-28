package com.example.sampleusbproject.presentation.commonViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaveParcelViewModel@Inject constructor(): ViewModel() {

    var receiverPhoneNumber: String = ""
    var phoneNumber: String = ""
    var selectedCell: SelectedCell ?= null
    var days: Int = -1
    var sumOfPay = 0
    var orderId: String = ""
    var transactionId: String = ""

}