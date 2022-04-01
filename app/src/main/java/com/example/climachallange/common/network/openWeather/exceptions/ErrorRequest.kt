package com.example.climachallange.common.network.openWeather.exceptions

import android.app.Application
import com.example.climachallange.R
import com.example.climachallange.common.utils.TypeError

class ErrorRequest(var typeError: Int, var errorMessage: String) : Exception()