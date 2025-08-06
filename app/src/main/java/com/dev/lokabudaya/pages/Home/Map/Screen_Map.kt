package com.dev.lokabudaya.pages.Map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val apiKey = stringResource(id = R.string.google_map_api_key)

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
    }

    val placesClient = remember { Places.createClient(context) }

    val defaultLocation = LatLng(-6.2088, 106.8456)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var showPredictions by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocationName by remember { mutableStateOf("") }
    
    // State for managing which marker is selected to show InfoWindow
    var selectedMarkerId by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(context) { location ->
                location?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                getCurrentLocation(context) { location ->
                    location?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                    }
                }
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false
            )
        ) {
            // Marker untuk lokasi yang dipilih user
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = selectedLocationName,
                    snippet = "Selected Location"
                )
            }
            
            // Marker untuk semua Tour locations (Blue markers)
            DataProvider.tourItemLists.forEachIndexed { index, tour ->
                if (tour.latitude != 0.0 && tour.longtitude != 0.0) {
                    val markerId = "tour_$index"
                    val markerPosition = LatLng(tour.latitude, tour.longtitude)
                    
                    Marker(
                        state = MarkerState(position = markerPosition),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                        onClick = {
                            if (selectedMarkerId == markerId) {
                                // Second click - navigate to detail page
                                val originalIndex = DataProvider.tourItemLists.indexOf(tour)
                                navController.navigate("DetailTourPage/$originalIndex")
                            } else {
                                // First click - set as selected
                                selectedMarkerId = markerId
                            }
                            true
                        },
                        title = tour.title,
                        snippet = "🏛️ Wisata • ${tour.location}"
                    )
                }
            }
            
            // Marker untuk semua Event locations (Purple/Magenta markers)
            DataProvider.eventItemLists.forEachIndexed { index, event ->
                if (event.latitude != 0.0 && event.longtitude != 0.0) {
                    val markerId = "event_$index"
                    val markerPosition = LatLng(event.latitude, event.longtitude)
                    
                    Marker(
                        state = MarkerState(position = markerPosition),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                        onClick = {
                            if (selectedMarkerId == markerId) {
                                // Second click - navigate to detail page
                                val originalIndex = DataProvider.eventItemLists.indexOf(event)
                                navController.navigate("DetailEventPage/$originalIndex")
                            } else {
                                // First click - set as selected
                                selectedMarkerId = markerId
                            }
                            true
                        },
                        title = event.title,
                        snippet = "🎉 Event • ${event.location}"
                    )
                }
            }
            
            // Marker untuk semua Culinary locations (Orange markers)
            DataProvider.kulinerItemLists.forEachIndexed { index, kuliner ->
                if (kuliner.latitude != 0.0 && kuliner.longtitude != 0.0) {
                    val markerId = "kuliner_$index"
                    val markerPosition = LatLng(kuliner.latitude, kuliner.longtitude)
                    
                    Marker(
                        state = MarkerState(position = markerPosition),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                        onClick = {
                            if (selectedMarkerId == markerId) {
                                // Second click - navigate to detail page
                                val originalIndex = DataProvider.kulinerItemLists.indexOf(kuliner)
                                navController.navigate("DetailCulinaryPage/$originalIndex")
                            } else {
                                // First click - set as selected
                                selectedMarkerId = markerId
                            }
                            true
                        },
                        title = kuliner.title,
                        snippet = "🍴 Kuliner • ${kuliner.location}"
                    )
                }
            }
        }

        // Legend at bottom right
        MapLegend(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Explore Map",
                    fontSize = 20.sp,
                    fontFamily = interBold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { query ->
                    searchText = query
                    if (query.isNotEmpty()) {
                        searchPlaces(placesClient, query) { results ->
                            predictions = results
                            showPredictions = true
                        }
                    } else {
                        predictions = emptyList()
                        showPredictions = false
                    }
                },
                placeholder = { Text("Search places") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            predictions = emptyList()
                            showPredictions = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_cross),
                                contentDescription = "Clear",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = selectedCategoryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (showPredictions && predictions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LazyColumn {
                        items(predictions) { prediction ->
                            PredictionItem(
                                prediction = prediction,
                                onClick = {
                                    getPlaceDetails(placesClient, prediction.placeId) { place ->
                                        place?.let {
                                            selectedLocation = it.latLng
                                            selectedLocationName = it.name ?: ""
                                            searchText = it.name ?: ""
                                            showPredictions = false
                                            predictions = emptyList()

                                            it.latLng?.let { latLng ->
                                                cameraPositionState.position =
                                                    CameraPosition.fromLatLngZoom(latLng, 15f)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionItem(
    prediction: AutocompletePrediction,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = prediction.getPrimaryText(null).toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = prediction.getSecondaryText(null).toString(),
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

@Composable
fun MapLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Legenda",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            // Tour marker legend
            LegendItem(
                color = Color(0xFF2196F3), // Blue
                label = "Wisata"
            )
            
            // Event marker legend
            LegendItem(
                color = Color(0xFFFF00FF), // Magenta/Purple (matches BitmapDescriptorFactory.HUE_MAGENTA)
                label = "Event"
            )
            
            // Culinary marker legend
            LegendItem(
                color = Color(0xFFFF9800), // Orange
                label = "Kuliner"
            )
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Black
        )
    }
}


fun searchPlaces(
    placesClient: PlacesClient,
    query: String,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    val token = AutocompleteSessionToken.newInstance()
    val request = FindAutocompletePredictionsRequest.builder()
        .setSessionToken(token)
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            onResult(emptyList())
        }
}

fun getPlaceDetails(
    placesClient: PlacesClient,
    placeId: String,
    onResult: (Place?) -> Unit
) {
    val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            onResult(response.place)
        }
        .addOnFailureListener { exception ->
            onResult(null)
        }
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: android.content.Context,
    onResult: (LatLng?) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            location?.let {
                onResult(LatLng(it.latitude, it.longitude))
            } ?: onResult(null)
        }
        .addOnFailureListener {
            onResult(null)
        }
}