package com.example.climachallange.mainModule.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.climachallange.BuildConfig.APPLICATION_ID
import com.example.climachallange.R
import com.example.climachallange.common.instances.SharedPreferenceInstance
import com.example.climachallange.common.utils.checkForInternet
import com.example.climachallange.databinding.ActivityMainBinding
import com.example.climachallange.mainModule.adapters.DailyWeatherAdapter
import com.example.climachallange.mainModule.model.Daily
import com.example.climachallange.mainModule.model.WeatherEntityDTO
import com.example.climachallange.mainModule.model.WeatherOneCallDTO
import com.example.climachallange.mainModule.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityError"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mSharedPreferences: SharedPreferenceInstance
    private lateinit var mLinearLayoutManager: RecyclerView.LayoutManager
    private lateinit var mDailyAdapter: DailyWeatherAdapter

    /**
     * Punto de entrada para el API Fused Location Provider.
     */
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var latitude = ""
    private var longitude = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeComponentsViewMain()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkForInternet(this)) {
            if (!checkPermissions()) {
                requestPermissions()
            } else {
                // despues de que se obtiene la location se ejecuta el setUpViewData con esa location
                getLastLocation() { location ->
                    setupViewModel()
                }
            }
        } else {
            showError(getString(R.string.no_internet_access))
            with(mBinding) {
                mainTvNoAccessInternet.isVisible = true
                mainFabSettings.isVisible = false
                mainFabInfo.isVisible = false
                mainTvDirection.isVisible = false
                mainClDataWeather.isVisible = false
            }
        }
    }

    private fun initializeComponentsViewMain() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = SharedPreferenceInstance.getInstance(this)
        mSharedPreferences.initializeComponents(this)
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val isFirstTime =
            false //mSharedPreferences.getStringValue(getString(R.string.first_time)).toBoolean()
        if (isFirstTime) {
            mMainViewModel.getWheatherByIdViewModel(
                SharedPreferenceInstance.getLongValue(getString(R.string.city_default_key)),
                SharedPreferenceInstance.getStringValue(getString(R.string.units_default_key))
                    .toString(),
                SharedPreferenceInstance.getStringValue(getString(R.string.lenguaje_default_key))
                    .toString(),
                SharedPreferenceInstance.getStringValue(getString(R.string.api_id)).toString()
            )
        } else {
            mMainViewModel.getWheatherByLocationViewModel(
                latitude,
                longitude,
                SharedPreferenceInstance.getStringValue(getString(R.string.units_default_key))
                    .toString(),
                SharedPreferenceInstance.getStringValue(getString(R.string.lenguaje_default_key))
                    .toString(),
                SharedPreferenceInstance.getStringValue(getString(R.string.api_id)).toString()
            )
        }

        mMainViewModel.mWheatherLiveData.observe(this) { respuesta ->
            setUi(respuesta)
        }

        mMainViewModel.mWheatherOneCallLiveData.observe(this) { respuesta ->
            setUiOneCall(respuesta)
        }
    }

    private fun setUiOneCall(respuesta: WeatherOneCallDTO?) {
        showIndicator(false)

        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mDailyAdapter = DailyWeatherAdapter(respuesta?.daily!!)

        mBinding.mainRvWeatherPronostic.apply {
            layoutManager = mLinearLayoutManager
            adapter = mDailyAdapter
        }

        mBinding.apply {
            mainTvDirection.text = respuesta?.name
            mainTvDate.text = respuesta?.date
            mainTvTemperatureNumber.text = respuesta?.temp.toString()
            mainTvTemperatureScale.text = "°C"
            mainTvWeatherCondition.text = respuesta?.weatherCondition
            mainTvSunrise.text = respuesta?.sunrise
            mainTvSunset.text = respuesta?.sunset
            mainTvFeelsLike.text = respuesta?.feelsLike
            mainTvWind.text = respuesta?.wind
            mainTvPressure.text = respuesta?.pressure
            mainTvHumidity.text = respuesta?.humidity
            Glide.with(this@MainActivity)
                .load(respuesta?.imgWeatherCondition)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mainIvWeatherCondition)
        }
    }

    private fun setUi(respuesta: WeatherEntityDTO) {
        mBinding.apply {
            mainTvDirection.text = respuesta.name
            mainTvDate.text = respuesta.date
            mainTvTemperatureNumber.text = respuesta.temp.toString()
            mainTvTemperatureScale.text = "°C"
            mainTvWeatherCondition.text = respuesta.weatherCondition
            mainTvSunrise.text = respuesta.sunrise
            mainTvSunset.text = respuesta.sunset
            mainTvFeelsLike.text = respuesta.feelsLike
            mainTvWind.text = respuesta.wind
            mainTvPressure.text = respuesta.pressure
            mainTvHumidity.text = respuesta.humidity
            Glide.with(this@MainActivity)
                .load(respuesta.imgWeatherCondition)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mainIvWeatherCondition)
        }
    }


    /**
     * Devuelve el estado de los permisos que se necesitan
     */

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Proporciona una explicación adicional al usuario (rationale). Esto ocurre si el usuario
            // niega el permiso previamente pero no marca la casilla "No volver a preguntar".
            Log.i(
                TAG,
                "Muestra explicación rationale para proveer una contexto adicional de porque se requiere el permiso"
            )
            showSnackbar(R.string.permission_rationale, android.R.string.ok) {
                // Solicitar permiso
                startLocationPermissionRequest()
            }

        } else {
            // Solicitar permiso. Es posible que esto pueda ser contestado de forma automática
            // si la configuración del dispositivo define el permiso a un estado predefinido o
            // si el usuario anteriormente activo "No presenter de nuevo".
            Log.i(TAG, "Solicitando permiso")
            startLocationPermissionRequest()
        }
    }

    /**
     * Callback recibido cuando se ha completado una solicitud de permiso.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                // Si el flujo es interrumpido, la solicitud de permiso es cancelada y se
                // reciben arrays vacios.
                grantResults.isEmpty() -> Log.i(TAG, "La interacción del usuario fue cancelada.")

                // Permiso otorgado.
                // Podemos pasar la referencia a una funcion si cumple con el mismo prototipo
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation(this::setupViewData)


                else -> {
                    showSnackbar(
                        R.string.permission_denied_explanation, R.string.settings
                    ) {
                        // Construye el intent que muestra la ventana de configuración del app.
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    /**
     * Provee un forma sencilla de obtener la ubicación del dispositivo, muy adecuada para
     * applicaciones que no requieren de una alta preción de la ubicación y que no requieran
     * actualizaciones. Obtiene lo mejor y y más reciente ubicación disponible, que en algunos
     * casos puede llegar a ser nula, cuando la ubicación no este disponible.
     *
     * La herramienta Lint checa el código del proyecto por bugs y propone optimizaciones.
     *
     * SuppressLint indica que Lint debe ignorar las alertas pera el elemento anotado.
     *
     * Nota: Este método debe llamarse después que los permispos de ubicación fueron otorgados.
     *
     * @param onLocation es un callback que recibirá la location obtenida por
     * fusedLocationClient.lastLocation
     */

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocation: (location: Location) -> Unit) {
        mFusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {
                    val location = taskLocation.result
                    latitude = location?.latitude.toString()
                    longitude = location?.longitude.toString()
                    Log.d(TAG, "GetLasLoc Lat: $latitude Long: $longitude")
                    onLocation(taskLocation.result)
                } else {
                    Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                    showSnackbar(R.string.no_location_detected)
                }
            }
    }

    private fun setupViewData(location: Location) {
        //TODO: Implementar metodo para guadar la locacion en los preferences
    }

    private fun showIndicator(visible: Boolean) {
        mBinding.mainPbIndicator.isVisible = visible
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content), getString(snackStrId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }


}