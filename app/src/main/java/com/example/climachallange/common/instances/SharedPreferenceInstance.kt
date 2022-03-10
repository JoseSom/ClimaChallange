package com.example.climachallange.common.instances

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceInstance {
    private val sharedPreference = SharedPreferenceInstance
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun getInstance(context: Context): SharedPreferenceInstance {
        sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        return sharedPreference
    }

    fun clean() {
        editor.clear()
        editor.apply()
    }
}