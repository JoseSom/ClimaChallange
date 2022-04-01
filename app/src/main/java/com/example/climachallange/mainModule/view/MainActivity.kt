package com.example.climachallange.mainModule.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.climachallange.BuildConfig.APPLICATION_ID
import com.example.climachallange.R
import com.example.climachallange.common.instances.SharedPreferenceInstance
import com.example.climachallange.common.utils.TAG
import com.example.climachallange.common.utils.Units.*
import com.example.climachallange.common.utils.checkForInternet
import com.example.climachallange.databinding.ActivityMainBinding
import com.example.climachallange.mainModule.adapters.DailyWeatherAdapter
import com.example.climachallange.mainModule.model.WeatherEntityDTO
import com.example.climachallange.mainModule.model.WeatherOneCallDTO
import com.example.climachallange.mainModule.viewModel.MainViewModel
import com.example.climachallange.settingsModule.view.SettingsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mSharedPreferences: SharedPreferenceInstance
    private lateinit var mPreferenceSettings: SharedPreferences

    private var isFirstTime: Boolean = false
    private var isNotByOneCall: Boolean = true

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
        setupObservers()
        startRequest()

    }

    private fun startRequest() {
        if (checkForInternet(this)) {
            if (!checkPermissions()) {
                requestPermissions()
            } else {
                isNotByOneCall = mSharedPreferences.getBooleanValue(getString(R.string.ubication_selected))
                if (!isNotByOneCall) {
                    getLastLocation() { location ->
                        setupOneCall(location)
                    }
                } else {
                    mBinding.mainRvWeatherPronostic.visibility = View.GONE
                    setupWeather()
                }
            }
        } else {
            showError(getString(R.string.no_internet_access))
            showMessageInternet(true)
        }
    }

    private fun getSettingsValues() {
        mSharedPreferences.setStringValue(
            getString(R.string.language_key),
            mPreferenceSettings.getString(getString(R.string.language_selected),getString(R.string.spanish_value)).toString()
        )

        mSharedPreferences.setStringValue(
            getString(R.string.unit_key),
            mPreferenceSettings.getString(getString(R.string.unit_selected),getString(R.string.metric)).toString()
        )

        mSharedPreferences.setBooleanValue(
            getString(R.string.ubication_selected),
            mPreferenceSettings.getBoolean(getString(R.string.ubication_selected),false)
        )

        Log.d(TAG,mPreferenceSettings.getString(getString(R.string.city_selected),
            3530597L.toString()
        ).toString())

        mSharedPreferences.setLongValue( getString(R.string.city_selected),
            mPreferenceSettings.getString(getString(R.string.city_selected), 3530597L.toString())!!.toLong()
        )
    }

    private fun showMessageInternet(isVisible: Boolean) {
        with(mBinding) {
            if (isVisible) {
                mainTvNoAccessInternet.isVisible = true
                mainPbIndicator.isVisible = true
                mainFabSettings.isVisible = false
                mainFabRefresh.isVisible = false
                mainTvDirection.isVisible = false
                mainClDataWeather.isVisible = false
            } else {
                mainTvNoAccessInternet.isVisible = false
                mainPbIndicator.isVisible = false
                mainFabSettings.isVisible = true
                mainFabRefresh.isVisible = true
                mainTvDirection.isVisible = true
                mainClDataWeather.isVisible = true
            }
        }

    }


    private fun initializeComponentsViewMain() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mPreferenceSettings = PreferenceManager.getDefaultSharedPreferences(this)
        mSharedPreferences = SharedPreferenceInstance.getInstance(this)

        isFirstTime = mSharedPreferences.getBooleanValue(getString(R.string.first_time))
        mSharedPreferences.initializeComponents(this, isFirstTime)

        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mBinding.mainFabSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        mBinding.mainFabRefresh.setOnClickListener {
            getSettingsValues()
            startRequest()
        }
    }

    private fun setupObservers() {
        mMainViewModel.mWheatherLiveData.observe(this) { respuesta ->
            setUi(respuesta)
        }

        mMainViewModel.mWheatherOneCallLiveData.observe(this) { respuesta ->
            setUiOneCall(respuesta)
        }

        mMainViewModel.getLoadingStatus().observe(this){ isVisible ->
            showIndicator(isVisible)
        }

        mMainViewModel.getTypeError().observe(this){ error ->
            showError(error.errorMessage)
        }
    }


    private fun setupOneCall(location: Location) {
        mSharedPreferences.setStringValue(
            getString(R.string.latitude),
            location.latitude.toString()
        )
        mSharedPreferences.setStringValue(
            getString(R.string.longitude),
            location.longitude.toString()
        )


        if (checkForInternet(this)) {
            showMessageInternet(false)
            mMainViewModel.setLoadingStatus(true)
            mMainViewModel.getWheatherByLocationViewModel(
                latitude,
                longitude,
                mSharedPreferences.getStringValue(getString(R.string.unit_key)).toString(),
                mSharedPreferences.getStringValue(getString(R.string.language_key)).toString(),
                mSharedPreferences.getStringValue(getString(R.string.api_id)).toString()
            )
        } else {
            showError(getString(R.string.no_internet_access))
            showMessageInternet(true)
        }

        getSettingsValues()
    }

    private fun setupWeather() {
        getSettingsValues()

        mMainViewModel.getWheatherByIdViewModel(
            mSharedPreferences.getLongValue(getString(R.string.city_key)),
            mSharedPreferences.getStringValue(getString(R.string.unit_key))
                .toString(),
            mSharedPreferences.getStringValue(getString(R.string.language_key))
                .toString(),
            mSharedPreferences.getStringValue(getString(R.string.api_id)).toString()
        )
    }

    private fun setUiOneCall(respuesta: WeatherOneCallDTO?) {
        mMainViewModel.setLoadingStatus(false)

        val unitTemperature = mSharedPreferences.getStringValue(getString(R.string.unit_key))
        val letterTemperature: String = unitTemperature?.let { unit ->
            when (unit) {
                IMPERIAL.nameUnit -> "°F"
                STANDARD.nameUnit -> "K"
                METRIC.nameUnit -> "°C"
                else -> ""
            }
        }.toString()


        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mDailyAdapter = DailyWeatherAdapter(respuesta?.daily!!)

        mBinding.mainRvWeatherPronostic.apply {
            visibility = View.VISIBLE
            layoutManager = mLinearLayoutManager
            adapter = mDailyAdapter
        }

        mBinding.apply {
            mainTvDirection.text = respuesta.name
            mainTvDate.text = respuesta.date
            mainTvTemperatureNumber.text = respuesta.temp
            mainTvTemperatureScale.text = letterTemperature
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

    private fun setUi(respuesta: WeatherEntityDTO) {
        val unitTemperature = mSharedPreferences.getStringValue(getString(R.string.unit_key))
        val letterTemperature: String = unitTemperature?.let { unit ->
            when (unit) {
                IMPERIAL.nameUnit -> "°F"
                STANDARD.nameUnit -> "K"
                METRIC.nameUnit -> "°C"
                else -> ""
            }
        }.toString()


        mBinding.apply {
            mainTvDirection.text = respuesta.name
            mainTvDate.text = respuesta.date
            mainTvTemperatureNumber.text = respuesta.temp.toString()
            mainTvTemperatureScale.text = letterTemperature
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
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation(this::setupOneCall)

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

    override fun onResume() {
        super.onResume()
        getSettingsValues()
    }


}