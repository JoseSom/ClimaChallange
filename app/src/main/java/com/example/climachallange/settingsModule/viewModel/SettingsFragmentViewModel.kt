package com.example.climachallange.settingsModule.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.ListPreference
import com.example.climachallange.settingsModule.model.SettingsInteractor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SettingsFragmentViewModel : ViewModel() {
    private var mInteractor: SettingsInteractor = SettingsInteractor()
}