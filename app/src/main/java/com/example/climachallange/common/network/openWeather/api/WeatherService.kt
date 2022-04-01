package com.example.climachallange.common.network.openWeather.api

import com.example.climachallange.mainModule.model.Current
import com.example.climachallange.mainModule.model.WeatherEntity
import com.example.climachallange.mainModule.model.WeatherOneCall
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByCityId(
        @Query("id") id: Long,
        @Query("units") units: String?,
        @Query("lang") lang: String?,  // Para el idioma
        @Query("appid") appid: String): Response<WeatherEntity>


    @GET("data/2.5/onecall")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String?,
        @Query("lang") lang: String?,  // Para el idioma
        @Query("appid") appid: String): Response<WeatherOneCall>

}