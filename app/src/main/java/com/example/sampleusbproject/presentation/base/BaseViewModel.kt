package com.example.sampleusbproject.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {

    private val _alertLiveData = MutableLiveData<Boolean>()
    val alertLiveData: LiveData<Boolean>
        get() = _alertLiveData

}