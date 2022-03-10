package com.example.climachallange.mainModule.model

import com.example.climachallange.common.instances.RetrofitInstance
import com.example.climachallange.common.network.openWeather.api.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainInteractor{
    private val retrofit = RetrofitInstance.getRetrofit().create(WeatherService::class.java)

    suspend fun getWeatherByCityId(id:Long,units:String,lang:String,appid:String): WeatherEntityDTO {
        return withContext(Dispatchers.IO){
            val response = retrofit.getWeatherByCityId(id,units,lang,appid)
            WeatherEntityMapper().fromEntity(response)
        }
    }

    suspend fun getWeatherByCoordinates(lat:String,lon:String,units:String,lang:String,appid:String): WeatherOneCallDTO {
        return withContext(Dispatchers.IO){
            val response = retrofit.getWeatherByCoordinates(lat,lon,units,lang,appid)
            WeatherOneCallMapper().fromEntity(response)
        }
    }

}
