package com.example.climachallange.common.instances

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.climachallange.R
import com.example.climachallange.common.utils.TAG

object SharedPreferenceInstance {
    private val sharedPreference = SharedPreferenceInstance
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    fun getInstance(context: Context): SharedPreferenceInstance {
        mSharedPreferences =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        editor = mSharedPreferences.edit()
        return sharedPreference
    }

    fun getStringValue(key: String): String? {
        return mSharedPreferences.getString(key, null)
    }

    fun getBooleanValue(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, true)
    }

    fun getLongValue(key: String): Long {
        return mSharedPreferences.getLong(key, -1)
    }

    fun setStringValue(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun setBooleanValue(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setLongValue(key: String, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    fun initializeComponents(context: Context, isFirstTime: Boolean) {
        with(editor) {
            Log.d(TAG,isFirstTime.toString())
            if (isFirstTime){
                putString(context.getString(R.string.lenguaje_default_key), context.getString(R.string.english))
                putString(context.getString(R.string.units_default_key), context.getString(R.string.imperial_units_key))
                putLong(context.getString(R.string.city_default_key), 3530597L)
            }else{
                putString(context.getString(R.string.lenguaje_default_key), getStringValue(context.getString(R.string.lenguaje_selected)))
                putString(context.getString(R.string.units_default_key), context.getString(R.string.imperial_units_key))
            }
            putString(context.getString(R.string.api_id), "c9034ac6e5d184a011185581d5c92da8")
            putBoolean(context.getString(R.string.first_time), false)
            apply()
        }
    }

    fun clean() {
        editor.clear()
        editor.apply()
    }
}