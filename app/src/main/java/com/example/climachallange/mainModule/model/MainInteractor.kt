package com.example.climachallange.mainModule.model

import android.util.Log
import com.example.climachallange.common.instances.RetrofitInstance
import com.example.climachallange.common.network.openWeather.api.WeatherService
import com.example.climachallange.common.network.openWeather.exceptions.ErrorRequest
import com.example.climachallange.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainInteractor {
    private val retrofit = RetrofitInstance.getRetrofit().create(WeatherService::class.java)

    suspend fun getWeatherByCityId(
        id: Long,
        units: String,
        lang: String,
        appid: String
    ): Response<WeatherEntity> {
        return withContext(Dispatchers.IO) {
            val response = retrofit.getWeatherByCityId(id, units, lang, appid)
            response
        }
    }

    suspend fun getWeatherByCoordinates(lat:String,lon:String,units:String,lang:String,appid:String): Response<WeatherOneCall> {
        return withContext(Dispatchers.IO){
            val response = retrofit.getWeatherByCoordinates(lat,lon,units,lang,appid)
            response
        }
    }

}
