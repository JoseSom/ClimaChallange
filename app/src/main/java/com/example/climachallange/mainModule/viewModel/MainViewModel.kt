package com.example.climachallange.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climachallange.mainModule.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception


class MainViewModel: ViewModel(){
    private var mInteractor: MainInteractor = MainInteractor()
    var mWheatherLiveData = MutableLiveData<WeatherEntityDTO>()
    var mWheatherOneCallLiveData = MutableLiveData<WeatherOneCallDTO>()

    fun getWheatherByIdViewModel(id:Long, units:String, lang:String, appid:String){
        viewModelScope.launch {
            val respuesta = mInteractor.getWeatherByCityId(id,units,lang,appid)
            mWheatherLiveData.postValue(respuesta)
        }
    }

    fun getWheatherByLocationViewModel(lat: String, lon:String, units:String, lang:String, appid:String){
        viewModelScope.launch {
            val respuesta = mInteractor.getWeatherByCoordinates(lat,lon,units,lang,appid)
            mWheatherOneCallLiveData.postValue(respuesta)
        }
    }

}