package com.example.fakestore.profile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale

@Composable
fun LocationScreen(location:(address:List<Address>)->Unit) {
    val context = LocalContext.current
var key by remember { mutableStateOf(false) }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, fetch location
            key = !key
        } else {
            // Permission denied
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Check and request permission
    LaunchedEffect(key) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION // ✅ Corrected
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            fetchLocation(context) { loc ->

                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                if (address != null) {
                    location(address)
                }
            }
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }


}