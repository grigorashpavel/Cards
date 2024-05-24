package com.pasha.core.progress_indicator.api

import android.app.Dialog
import android.app.KeyguardManager.KeyguardDismissCallback
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator


class ProgressIndicator : DialogFragment() {
    private var cancelCallback: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog()")


        val indicator = CircularProgressIndicator(requireContext()).apply {
            isIndeterminate = true
            indicatorSize = 128.dpToPx()
            trackThickness = 8.dpToPx()
        }

        return Dialog(requireContext()).apply {
            setContentView(indicator)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        Log.d(TAG, "onCancel()")

        cancelCallback?.invoke()
    }

    fun setCallback(callback: () -> Unit) {
        cancelCallback = callback
    }

    override fun onStop() {
        super.onStop()
        dismissAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "onDismiss()")
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    companion object {
        const val TAG = "ProgressDialogIndicator"
    }
}