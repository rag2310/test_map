package com.rago.testmap

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.rago.testmap.ui.theme.TestMapTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestMapTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Greeting(name: String = "a todos") {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val fusedLocationClient by remember {
        mutableStateOf(LocationServices.getFusedLocationProviderClient(context))
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true
            )
        )
    }

    val cameraPositionState = rememberCameraPositionState {}

    refreshLocation(fusedLocationClient, cameraPositionState, scope)

    Scaffold(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            GoogleMap(
                Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
            }
        }
    }
}

fun refreshLocation(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    scope: CoroutineScope
) {
    val handler = Handler()

    handler.postDelayed(object : Runnable {
        @SuppressLint("MissingPermission")
        override fun run() {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                Log.i("Greeting", "Greeting: ${it.latitude} - ${it.longitude})")

                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                ), 18f
                            )
                        ),
                        1000
                    )
                }
            }

            handler.postDelayed(this, 1000)
        }
    }, 1000)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestMapTheme {
        Greeting("Android")
    }
}