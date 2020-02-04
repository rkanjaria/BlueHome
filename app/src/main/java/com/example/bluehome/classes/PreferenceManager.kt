package com.example.bluehome.classes

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getInt(key: String, defaultValue: Int) = sharedPreferences.getInt(key, defaultValue)
    fun getString(key: String, defaultValue: String) =
        sharedPreferences.getString(key, defaultValue)

    fun getBoolean(key: String, defaultValue: Boolean) =
        sharedPreferences.getBoolean(key, defaultValue)

    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun remove(key: String) {
        editor.remove(key).apply()
    }

    fun clearPreferences() {
        editor.clear().apply()
    }
}

