package com.infomantri.bhakti.bhajan.Utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> AppCompatActivity.initViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.initViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}