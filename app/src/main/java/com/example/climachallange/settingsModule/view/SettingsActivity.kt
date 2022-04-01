package com.example.climachallange.settingsModule.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.climachallange.R
import com.example.climachallange.common.utils.TAG
import com.example.climachallange.common.utils.getJsonDataFromAsset
import com.example.climachallange.settingsModule.viewModel.SettingsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var mSettingsFragmentViewModel: SettingsFragmentViewModel

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            mSettingsFragmentViewModel =
                ViewModelProvider(this)[SettingsFragmentViewModel::class.java]
            val listPreference = findPreference<ListPreference>(getString(R.string.city_selected))

            val json = getJsonDataFromAsset(requireContext(), "city_list.json")
            val entries = arrayListOf<String>()
            val entryValues = arrayListOf<String>()

            try {
                val jsonArray = JSONObject(json).getJSONArray("city")
                for (i in 0 until jsonArray.length()) {
                    val cityObject = jsonArray[i] as JSONObject
                    entryValues.add(cityObject.getString("id"))
                    entries.add(cityObject.getString("name"))
                }
            } catch (excepcion: JSONException) {
                Log.d(TAG, "Improper JSON string")
            }

            listPreference?.entries = entries.toTypedArray()
            listPreference?.entryValues = entryValues.toTypedArray()

        }
    }
}