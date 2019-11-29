package com.syngenta.pack.custom

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialog
import com.infomantri.bhakti.bhajan.R

/**
 * Dialog with custom background dim.
 */
class CustomDimDialog(context: Context?) :
        AppCompatDialog(context, R.style.Theme_CustomAlertDialog) {

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.apply {
            // Spread the dialog as large as the screen.
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        //Uncomment to make it non-closable when clicked outside the alert dialog
        /*window?.apply {
            Log.e("DATA","Inside")
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val params = attributes
            params.gravity = Gravity.CENTER
//            params.flags = params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            setAttributes(params)
        }*/
    }


    override fun setContentView(view: View) {
        super.setContentView(wrap(view))
    }

    private fun wrap(content: View): View {
        val res = context.resources
        val verticalMargin = res.getDimensionPixelSize(R.dimen.dialog_vertical_margin)
        val horizontalMargin = res.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
        return FrameLayout(context).apply {
            addView(content, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin)
                gravity = Gravity.CENTER
            })
            val rect = Rect()
            setOnTouchListener { _, event ->
                when (event.action) {
                    // The FrameLayout is technically inside the dialog, but we treat it as outside.
                    MotionEvent.ACTION_DOWN -> {
                        content.getGlobalVisibleRect(rect)
                        if (!rect.contains(event.x.toInt(), event.y.toInt())) {
                            cancel()
                            true
                        } else {
                            false
                        }
                    }
                    else -> {
                        false
                    }
                }
            }
//            background = ColorDrawable(ResourcesCompat.getColor(res, R.color.scrim, context.theme))
        }
    }
}
