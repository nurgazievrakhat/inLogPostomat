package com.example.sampleusbproject.presentation.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.sampleusbproject.utils.customview.ProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModelFragment<ViewModel : BaseViewModel, Binding : ViewBinding>(
    @LayoutRes layoutId: Int,
    private val className: Class<ViewModel>,
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> Binding
) : BaseFragment<Binding>(layoutId, inflate) {

    val viewModel: ViewModel by lazy {
        ViewModelProvider(this)[className]
    }
    protected val alertDialog: ProgressDialog by lazy {
        return@lazy ProgressDialog()
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.alertLiveData.observe(viewLifecycleOwner) { showing ->
            try {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (showing) {
                        alertDialog.show(parentFragmentManager, "test")
                    } else {
                        alertDialog.dismiss()
                    }
                }
            } catch (e: Exception) {
                Log.e("sdfsdfsdfds", "show: ${e.message}")
            }
        }

        initialize()
        setupListeners()
        setupRequests()
        setupSubscribers()
    }

    protected open fun initialize() {
    }

    protected open fun setupListeners() {
    }

    protected open fun setupRequests() {
    }

    protected open fun setupSubscribers() {
    }

}