package com.infomantri.bhakti.bhajan

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.syngenta.pack.custom.DialogCustomFragment
import kotlinx.android.synthetic.main.custom_toolbar.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun getSharedPreference(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this)

    inline fun apiCall(networkFun: () -> Unit) {
        if (isNetworkAvailable()) {
            networkFun()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
            } else {
                showAlert(message = getString(R.string.alert_check_for_internet))
            }

        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false

    }

    fun showAlert(
        message: String? = null,
        title: String? = null,
        positiveBtnText: String? = getString(R.string.alert_ok),
        negativeBtnText: String? = null,
        negativeFunc: () -> Unit = { },
        positiveFunc: () -> Unit = { }

    ) {

        val dialog = DialogCustomFragment.invoke(
            title,
            message,
            positiveBtnText,
            negativeBtnText,
            positiveButton { positiveFunc() },
            negativeButton { negativeFunc() })

        dialog.isCancelable = true
        dialog.show(supportFragmentManager, "dismissible_alert")
    }

    private fun positiveButton(func: () -> Unit): DialogCustomFragment.OnCustomAlertPositiveClick {
        return object : DialogCustomFragment.OnCustomAlertPositiveClick {
            override fun onPositiveClick() {
                func()
            }
        }
    }

    private fun negativeButton(func: () -> Unit): DialogCustomFragment.OnCustomAlertNegativeClick {
        return object : DialogCustomFragment.OnCustomAlertNegativeClick {
            override fun onNegativeClick() {
                func()
            }
        }
    }

    fun Toolbar.setToolbar(
        showBackNav: Boolean = false,
        titleColor: Int = R.color.colorPrimary,
        title: String? = null,
        bgColor: Int = R.color.white
    ) {
//        setSupportActionBar(this)

//        toolIvSettings.visibility = if (showBackNav) View.GONE else View.VISIBLE
//        supportActionBar?.apply {
//            title = ""
//        }

        title?.let {
            toolTvCenterTitle?.text = it
            toolTvCenterTitle?.setTextColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    titleColor
                )
            )
        }

        toolbar.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, bgColor))
    }

    inline fun Toolbar.handleToolBackPress(crossinline func: () -> Unit) {
        setNavigationOnClickListener {
            func()
        }
    }

    fun startActivityFromLeft(activity: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, activity)
        if (bundle != null) {
            intent.putExtras(bundle)
        }

        startActivity(intent)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    fun launchActivityWithClearStack(activity: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        finish()
    }

    inline fun delayHandler(delayedMilliSec: Long, crossinline func: () -> Unit) {
        Handler().postDelayed({
            func()
        }, delayedMilliSec)
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun TextInputLayout.setError(editText: EditText, errorMessage: String) {
        this.error = errorMessage
        editText.requestFocus()
    }

    fun toast(
        message: String,
        context: Context = this@BaseActivity,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(context, message, duration).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

}