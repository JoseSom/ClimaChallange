package com.example.climachallange.settingsModule.model

import com.example.climachallange.common.instances.RetrofitInstance
import com.example.climachallange.common.network.openWeather.api.WeatherService
import com.example.climachallange.mainModule.model.Weather
import com.example.climachallange.mainModule.model.WeatherEntity
import com.example.climachallange.mainModule.model.WeatherOneCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class SettingsInteractor {
    val retrofit = RetrofitInstance.getRetrofit().create(WeatherService::class.java)

    suspend fun getWeatherByCityId(id:Long,units:String,lang:String,appid:String): WeatherEntity {
        return withContext(Dispatchers.IO){
            val response = retrofit.getWeatherByCityId(id,units,lang,appid)
            response
        }
    }

    suspend fun getWeatherByCoordinates(lat:String,lon:String,units:String,lang:String,appid:String): WeatherOneCall {
        return withContext(Dispatchers.IO){
            val response = retrofit.getWeatherByCoordinates(lat,lon,units,lang,appid)
            response
        }
    }
}