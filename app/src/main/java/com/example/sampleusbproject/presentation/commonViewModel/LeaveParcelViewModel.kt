package com.example.sampleusbproject.presentation.commonViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaveParcelViewModel@Inject constructor(): ViewModel() {

    var phoneNumber: String = ""

}