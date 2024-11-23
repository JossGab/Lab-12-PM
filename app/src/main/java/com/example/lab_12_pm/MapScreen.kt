package com.example.lab_12_pm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val arequipaLocation = LatLng(-16.4040102, -71.559611) // Coordenadas de Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(arequipaLocation, 12f)
    }

    // Define los puntos del triángulo
    val trianglePoints = listOf(
        LatLng(-16.4040102, -71.559611),  // Punto 1
        LatLng(-16.4100102, -71.555611),  // Punto 2
        LatLng(-16.4040102, -71.550611),  // Punto 3
        LatLng(-16.4040102, -71.559611)   // Vuelve al punto 1 para cerrar el triángulo
    )

    fun resizedMarkerIcon(): BitmapDescriptor {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.custom_marker)
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.4040102, -71.559611), 12f),
            durationMs = 3000
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Marcador principal en Arequipa
            Marker(
                state = rememberMarkerState(position = arequipaLocation),
                icon = resizedMarkerIcon(),
                title = "Arequipa, Perú"
            )

            // Triángulo con líneas verdes
            Polyline(
                points = trianglePoints,
                color = Color.Green,
                width = 5f
            )
        }
    }
}
