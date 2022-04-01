package com.example.climachallange.mainModule.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climachallange.common.network.openWeather.exceptions.ErrorRequest
import com.example.climachallange.common.utils.TypeError
import com.example.climachallange.mainModule.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception


class MainViewModel : ViewModel() {
    private var mInteractor: MainInteractor = MainInteractor()
    private val mError: MutableLiveData<ErrorRequest> = MutableLiveData()
    private val mLoadingStatus: MutableLiveData<Boolean> = MutableLiveData()

    var mWheatherLiveData = MutableLiveData<WeatherEntityDTO>()
    var mWheatherOneCallLiveData = MutableLiveData<WeatherOneCallDTO>()

    fun getWheatherByIdViewModel(id: Long, units: String, lang: String, appid: String) {
        viewModelScope.launch {
            try {
                val response = mInteractor.getWeatherByCityId(id, units, lang, appid)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val result = WeatherEntityMapper().fromEntity(it)
                        mWheatherLiveData.postValue(result)
                        setLoadingStatus(false)
                    }
                } else {
                    when (response.code()) {
                        TypeError.NOT_FOUND -> throw ErrorRequest(
                            TypeError.NOT_FOUND,
                            response.message()
                        )
                        TypeError.INTERNAL_ERROR_SERVER -> throw ErrorRequest(
                            TypeError.INTERNAL_ERROR_SERVER,
                            response.message()
                        )
                        else -> throw ErrorRequest(response.code(), response.message())
                    }
                }
            } catch (exception: ErrorRequest) {
                mError.value = ErrorRequest(exception.typeError, exception.errorMessage)
            }

        }
    }

    fun getWheatherByLocationViewModel(
        lat: String,
        lon: String,
        units: String,
        lang: String,
        appid: String
    ) {
        viewModelScope.launch {
            try {
                val response = mInteractor.getWeatherByCoordinates(lat, lon, units, lang, appid)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val result = WeatherOneCallMapper().fromEntity(it)
                        mWheatherOneCallLiveData.postValue(result)
                        setLoadingStatus(false)
                    }
                } else {
                    when (response.code()) {
                        TypeError.NOT_FOUND -> throw ErrorRequest(
                            TypeError.NOT_FOUND,
                            response.message()
                        )
                        TypeError.INTERNAL_ERROR_SERVER -> throw ErrorRequest(
                            TypeError.INTERNAL_ERROR_SERVER,
                            response.message()
                        )
                        else -> throw ErrorRequest(response.code(), response.message())
                    }
                }
            } catch (exception: ErrorRequest) {
                mError.value = ErrorRequest(exception.typeError, exception.errorMessage)
            }
        }
    }

    fun setLoadingStatus(isVisible: Boolean) {
        mLoadingStatus.value = isVisible
    }

    fun getLoadingStatus(): LiveData<Boolean> {
        return mLoadingStatus
    }

    fun getTypeError(): LiveData<ErrorRequest> {
        return mError
    }

}