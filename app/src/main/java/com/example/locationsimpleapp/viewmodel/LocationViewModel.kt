package com.example.locationsimpleapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.locationsimpleapp.model.LocationData

class LocationViewModel: ViewModel() {

    private val _locationDataState = mutableStateOf<LocationData?>(null)
    val locationDataState: State<LocationData?> = _locationDataState

    fun updateLocation(newLocation: LocationData){
        _locationDataState.value = newLocation
    }
}