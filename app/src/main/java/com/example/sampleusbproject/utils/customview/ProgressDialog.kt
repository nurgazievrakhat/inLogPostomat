package com.example.sampleusbproject.utils.customview

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.WindowManager
import com.example.sampleusbproject.databinding.ProgressDialogBinding

class ProgressDialog: BaseProgressDialog<ProgressDialogBinding>(ProgressDialogBinding::inflate, Type.Progress) {

    override fun onResume() {
        super.onResume()

        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params

        requireDialog().setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun dismiss() {
        if (isShowing) {
            super.dismiss()
        }
    }

}