package com.example.culturegram

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class GPS(private val context: Context) {
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var currentLocation = mutableStateOf<Location?>(null)
        private set

    private lateinit var locationCallback: LocationCallback

    @Composable
    fun GetCurrentLocation() {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                fetchLastKnownLocation()  // 最後に取得された位置情報をまず取得する
                startLocationUpdates()    // その後リアルタイムで更新する
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fetchLastKnownLocation()  // 最後に取得された位置情報をまず取得する
                startLocationUpdates()    // その後リアルタイムで更新する
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // 最後に取得された位置情報を取得するメソッド
    private fun fetchLastKnownLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation.value = it
                }
            }
        } catch (e: SecurityException) {
            Log.e("GPS", "Location permission error", e)
        }
    }

    // リアルタイムの位置情報更新
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation.value = locationResult.lastLocation
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
            )
        } catch (e: SecurityException) {
            Log.e("GPS", "Location permission error", e)
        }
    }

    fun stopLocationUpdates() {
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}