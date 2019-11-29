package com.syngenta.pack.custom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.infomantri.bhakti.bhajan.R
import kotlinx.android.synthetic.main.dialog_custom_alert.*

class DialogCustomFragment : CustomDimDialogFragment() {

    companion object {

        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val POSITIVE_BUTTON = "positive_button"
        private const val NEGATIVE_BUTTON = "negative_button"

        private var mPositiveButtonClickListener: OnCustomAlertPositiveClick? = null
        private var mNegativeButtonClickListener: OnCustomAlertNegativeClick? = null

        @JvmStatic
        fun invoke(
                title: String?,
                message: String?,
                positiveButtonText: String?,
                negativeButtonText: String?,
                positiveAlert: OnCustomAlertPositiveClick,
                negativeClick: OnCustomAlertNegativeClick
        ): DialogCustomFragment {

            val dialogCustomFragment = DialogCustomFragment()
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            bundle.putString(MESSAGE, message)
            bundle.putString(POSITIVE_BUTTON, positiveButtonText)
            bundle.putString(NEGATIVE_BUTTON, negativeButtonText)

            dialogCustomFragment.arguments = bundle

            mPositiveButtonClickListener = positiveAlert
            mNegativeButtonClickListener = negativeClick

            return dialogCustomFragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_custom_alert, container, false)

        setStyle(DialogFragment.STYLE_NO_INPUT, R.style.Theme_CustomAlertDialog)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.e("DATA", "On Activity" + this.dialog)

        val intent = arguments
        intent?.getString(TITLE)?.let {
            tvAlertTitle.visibleVisibility()
            tvAlertTitle.text = it
        }

        intent?.getString(MESSAGE)?.let {
            tvAlertMessage.text = it
        }

        intent?.getString(NEGATIVE_BUTTON)?.let {

            btNegative.visibleVisibility()
            btNegative.text = it
        }

        if (intent?.getString(NEGATIVE_BUTTON) == null) {
            btPositiveNoBorder.text = intent?.getString(POSITIVE_BUTTON)
            btPositive.goneVisibility()
        } else {
            btPositive.text = intent.getString(POSITIVE_BUTTON)
            btPositive.visibleVisibility()
        }

        btPositive?.setOnClickListener {
            mPositiveButtonClickListener?.onPositiveClick()
            dismiss()
        }

        btPositiveNoBorder?.setOnClickListener {
            mPositiveButtonClickListener?.onPositiveClick()
            dismiss()
        }

        btNegative?.setOnClickListener {
            mNegativeButtonClickListener?.onNegativeClick()
            dismiss()
        }
    }

    private fun View.visibleVisibility() {
        this.visibility = View.VISIBLE
    }

    private fun View.goneVisibility() {
        this.visibility = View.GONE
    }

    interface OnCustomAlertPositiveClick {
        fun onPositiveClick()
    }

    interface OnCustomAlertNegativeClick {
        fun onNegativeClick()
    }
}