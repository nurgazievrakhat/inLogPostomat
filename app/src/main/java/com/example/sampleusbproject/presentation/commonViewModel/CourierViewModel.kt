package com.example.sampleusbproject.presentation.commonViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourierViewModel@Inject constructor(): ViewModel() {

    var orderId: String = ""
    var cell: SelectedCell ?= null

}