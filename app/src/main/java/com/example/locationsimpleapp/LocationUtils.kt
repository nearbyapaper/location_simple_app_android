package com.example.locationsimpleapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.locationsimpleapp.model.LocationData
import com.example.locationsimpleapp.viewmodel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(
    private val context: Context
) {
    private val _fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    // Ignore Lint Check MissingPermission cuz we check permission before
    fun requestCurrentLocation(viewModel: LocationViewModel){
        // set handle callback on location request
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val currentLocation = LocationData(it.latitude,it.longitude)
                    viewModel.updateLocation(currentLocation)
                }
            }
        }

        // set location request
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()

        // get current location
        _fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    fun reverseGeoCodeLocation(locationData: LocationData):String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinates = LatLng(locationData.latitude,locationData.longitude)
        val addresses = geocoder.getFromLocation(coordinates.latitude,coordinates.longitude,1)
        return addresses?.get(0)?.getAddressLine(0).toString()
    }
}