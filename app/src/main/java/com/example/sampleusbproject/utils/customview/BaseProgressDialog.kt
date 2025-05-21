package com.example.sampleusbproject.utils.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseProgressDialog<Binding: ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> Binding, private var type: Type = Type.Notification): DialogFragment() {

    companion object {
        var dialogs: ArrayList<DialogFragment> = ArrayList()
    }

    private val job = Job()

    val uiScope = CoroutineScope(Dispatchers.Main + job) // UI

    private var _binding: Binding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = inflate.invoke(LayoutInflater.from(context),container,false)
        return  binding.root
    }

    var isShowing: Boolean = false

    override fun show(manager: FragmentManager, tag: String?) {
        if(isShowing) {
            return
        } else {
            super.show(manager, tag)
            isShowing = true
        }
    }

    override fun dismiss() {
        if (isShowing) {
            super.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        job.cancel()
    }

    enum class Type {
        Notification,
        Progress
    }
}