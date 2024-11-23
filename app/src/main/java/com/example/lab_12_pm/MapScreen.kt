package com.example.lab_12_pm

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val arequipaLocation = LatLng(-16.4040102, -71.559611)

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(arequipaLocation, 12f)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapProperties = MapProperties(mapType = mapType)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        userLocation = userLatLng
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    }
                }
            }
        }
    )

    fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    userLocation = userLatLng
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Coordenadas para el triángulo
    val trianglePoints = listOf(
        LatLng(-16.4040102, -71.559611),  // Vértice 1
        LatLng(-16.4100102, -71.555611),  // Vértice 2
        LatLng(-16.4040102, -71.550611),  // Vértice 3
        LatLng(-16.4040102, -71.559611)   // Cerrar el triángulo
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties
        ) {
            // Marcador principal
            Marker(
                state = rememberMarkerState(position = arequipaLocation),
                icon = resizedMarkerIcon(context),
                title = "Arequipa, Perú"
            )

            // Marcador de la ubicación actual
            userLocation?.let {
                Marker(
                    state = rememberMarkerState(position = it),
                    title = "Ubicación Actual"
                )
            }

            // Triángulo de líneas verdes
            Polyline(
                points = trianglePoints,
                color = Color.Green,
                width = 5f
            )
        }

        // Botones de cambio de tipo de mapa y ubicación actual
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val buttonColor = Color(0xFF6200EA) // Morado

            Button(onClick = { mapType = MapType.NORMAL }, colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text("Normal")
            }
            Button(onClick = { mapType = MapType.SATELLITE }, colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text("Satellite")
            }
            Button(onClick = { mapType = MapType.HYBRID }, colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text("Hybrid")
            }
            Button(onClick = { mapType = MapType.TERRAIN }, colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text("Terrain")
            }
            Button(
                onClick = { getCurrentLocation(fusedLocationClient) },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text("UbActual")
            }
        }
    }
}

@Composable
fun resizedMarkerIcon(context: android.content.Context): BitmapDescriptor {
    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.custom_marker)
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
}
