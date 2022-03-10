package com.example.climachallange.common.instances

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.climachallange.R

object SharedPreferenceInstance{
    private val sharedPreference = SharedPreferenceInstance
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    fun initializeComponents(context: Context) {
        with(editor){
            putString(context.getString(R.string.api_id),"c9034ac6e5d184a011185581d5c92da8")
            putString(context.getString(R.string.lenguaje_default_key),context.getString(R.string.english))
            putString(context.getString(R.string.units_default_key),context.getString(R.string.imperial_units_key))
            putLong(context.getString(R.string.city_default_key),3530597L)
            putBoolean(context.getString(R.string.first_time),true)
            apply()
        }
    }

    fun getInstance(context: Context): SharedPreferenceInstance {
        mSharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        editor = mSharedPreferences.edit()
        return sharedPreference
    }

    fun getStringValue(key : String): String?{
        return mSharedPreferences.getString(key,null)
    }

    fun getBooleanValue(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun getLongValue(key:String): Long{
        return mSharedPreferences.getLong(key,-1)
    }

    fun clean() {
        editor.clear()
        editor.apply()
    }
}