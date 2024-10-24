package com.example.locationsimpleapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.locationsimpleapp.ui.theme.LocationSimpleAppTheme
import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationsimpleapp.viewmodel.LocationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val locationViewModel: LocationViewModel = viewModel()
            LocationSimpleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = locationViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier, viewModel:LocationViewModel) {

    val context = LocalContext.current
    val locationUtils = LocationUtils(context)

    LocationScreen(locationUtils = locationUtils, context = context, viewModel, modifier)
}

@Composable
fun LocationScreen(
    locationUtils: LocationUtils,
    context: Context,
    viewModel: LocationViewModel,
    modifier: Modifier
) {

    val reverseGeoCodeLocation = if(viewModel.locationDataState.value != null) {
        locationUtils.reverseGeoCodeLocation(viewModel.locationDataState.value!!)
    }else{
        ""
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // I HAVE ACCESS to location
                locationUtils.requestCurrentLocation(viewModel)
            } else {
                // ASK for PERMISSION
                val ratinaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (ratinaleRequired) {
                    Toast.makeText(
                        context,
                        "Location permission required for this feature",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Go to settings and enable location permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(viewModel.locationDataState.value != null){
            Text(text = "Your location is ${viewModel.locationDataState.value?.latitude}, ${viewModel.locationDataState.value?.longitude}")
            Text(text = "Address is $reverseGeoCodeLocation")
        }else{
            Text(text = "Location is not available")
        }

        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)) {
                // access location
                Toast.makeText(
                    context,
                    "Location Permissions already granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // ask permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text("Get Location")
        }
    }
}
