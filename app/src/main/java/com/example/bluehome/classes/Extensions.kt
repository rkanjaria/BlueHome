package com.example.bluehome.classes

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)


fun AppCompatActivity.addFragmentInActivity(frameId: Int, fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(frameId, fragment, tag)
    }
}

fun AppCompatActivity.replaceFragmentInActivity(frameId: Int, fragment: Fragment) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragmentWithBackstack(frameId: Int, fragment: Fragment) {
    supportFragmentManager.transact {
        replace(frameId, fragment).addToBackStack(null)
    }
}

inline fun FragmentManager.transact(action: FragmentTransaction. () -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun Context.toast(message: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, time).show()
}

fun Context.getPreferenceManager() = PreferenceManager(this)