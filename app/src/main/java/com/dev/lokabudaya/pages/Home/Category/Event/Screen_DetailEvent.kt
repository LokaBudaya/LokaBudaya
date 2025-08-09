package com.dev.lokabudaya.pages.Home.Category.Event

import NetworkImage
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.TicketOrder
import com.dev.lokabudaya.data.TicketType
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.pages.Ticket.TicketViewModel
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

fun formatPrice(price: Int): String {
    return if (price <= 0) {
        "FREE"
    } else {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(price)
    }
}

@Composable
fun DetailEventPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    eventItem: EventItem,
    ticketViewModel: TicketViewModel
) {
    val context = LocalContext.current
    val apiKey = stringResource(id = R.string.google_map_api_key)
    val coroutineScope = rememberCoroutineScope()

    // Ticket Type
    val ticketTypes = remember {
        listOf(
            TicketType(
                id = "dewasa",
                name = "Tiket Dewasa",
                price = eventItem.price,
                description = "Tiket khusus dewasa di atas 17 tahun",
                maxQuantity = 10
            ),
            TicketType(
                id = "anak",
                name = "Tiket Anak",
                price = eventItem.price - 10000,
                description = "Tiket khusus anak di bawah 17 tahun",
                maxQuantity = 10
            )
        )
    }

    // Menyimpan jumlah tiket yang dibeli
    var ticketOrders by remember {
        mutableStateOf(ticketTypes.map { TicketOrder(it, 0) })
    }
    val totalQuantity = ticketOrders.sumOf { it.quantity }
    val totalPrice = ticketOrders.sumOf { it.totalPrice }


    val previewImages = listOf(
        eventItem.imgRes,
        eventItem.imgRes,
        eventItem.imgRes
    ).take(3)

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, apiKey)
        }
    }

    val eventLocation = LatLng(eventItem.latitude, eventItem.longtitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(eventLocation, 15f)
    }

    LaunchedEffect(eventLocation) {
        coroutineScope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(eventLocation, 16f, 0f, 0f)
                ),
                durationMs = 1500
            )
        }
    }

    Box(
        modifier= Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = if (totalQuantity > 0) 80.dp else 0.dp)
        ) {
            item {
                DetailEventItem(modifier, navController, authViewModel, eventItem, ticketViewModel)

                HorizontalDivider(
                    modifier = Modifier.alpha(.2f)
                )

                DetailOverview(eventItem)

                HorizontalDivider(
                    modifier = Modifier.alpha(.2f)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Ticket",
                        fontFamily = interBold,
                        fontSize = 20.sp
                    )
                }
            }
            items(ticketOrders) { ticketOrder ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TicketToBuy(
                            ticketOrder = ticketOrder,
                            onQuantityChange = { newQuantity ->
                                ticketOrders = ticketOrders.map { order ->
                                    if (order.ticketType.id == ticketOrder.ticketType.id) {
                                        order.copy(quantity = newQuantity)
                                    } else {
                                        order
                                    }
                                }
                            }
                        )
                    }
                }
            }
            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.alpha(.2f)
                )

                Column (
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Preview",
                        fontSize = 20.sp,
                        fontFamily = interBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(previewImages) { imageRes ->
                            Card(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { /* Handle image click */ },
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                NetworkImage(
                                    imageUrl = eventItem.imageUrl,
                                    fallbackRes = imageRes,
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Maps",
                        fontSize = 20.sp,
                        fontFamily = interBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(
                                isMyLocationEnabled = false
                            ),
                            uiSettings = MapUiSettings(
                                myLocationButtonEnabled = false,
                                zoomControlsEnabled = true,
                                compassEnabled = true,
                                mapToolbarEnabled = false
                            )
                        ) {
                            Marker(
                                state = MarkerState(position = eventLocation),
                                title = eventItem.title,
                                snippet = eventItem.location
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Go Now Section
                    GoNowSectionEvent(eventItem)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (totalQuantity > 0) {
            PurchaseSummaryBottom(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxWidth(),
                totalQuantity = totalQuantity,
                totalPrice = totalPrice,
                onPurchaseClick = {
                    ticketViewModel.updateTicketOrdersEvent(ticketOrders, eventItem)
                    navController.navigate("MidtransPaymentPage")
                }
            )
        }
    }
}

@Composable
fun DetailEventItem(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    eventItem: EventItem,
    ticketViewModel: TicketViewModel = viewModel()
) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    var isFavorite by remember { mutableStateOf(eventItem.isFavorite) }
    LaunchedEffect(favoriteItems) {
        isFavorite = favoriteViewModel.getFavoriteState(eventItem)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        NetworkImage(
            imageUrl = eventItem.imageUrl,
            fallbackRes = eventItem.imgRes,
            contentDescription = eventItem.title,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.0f),
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 1f)
                        ),
                        startY = 100f
                    )
                )
        )

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                favoriteViewModel.toggleFavorite(eventItem)
                isFavorite = favoriteViewModel.getFavoriteState(eventItem)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Red else Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = .2f),
                            Color.White.copy(alpha = .3f),
                            Color.White.copy(alpha = 1f)
                        )
                    )
                )
                .padding(horizontal=16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.6f),
                    text = eventItem.title,
                    fontSize = 48.sp,
                    lineHeight = 48.sp,
                    fontFamily = interBold,
                    color = Color(0xFF01103A)
                )
                Box(
                    modifier = Modifier
                        .offset(x = 24.dp)
                        .size(120.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape, clip = false)
                        .clip(CircleShape)
                        .weight(.3f)
                        .background(Color(0xFFD5C578)),
                    contentAlignment = Alignment.Center
                ) {
                    FormatEventDate(
                        startDate = eventItem.startDate,
                        endDate = eventItem.endDate,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 56.dp)
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(.85f)
                        .fillMaxHeight()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Time",
                            fontSize = 12.sp,
                            fontFamily = interBold,
                            color = Color.Black,
                        )
                        Text(
                            text = eventItem.eventTime,
                            fontSize = 20.sp,
                            fontFamily = interBold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Location",
                                fontSize = 12.sp,
                                fontFamily = interBold,
                                color = Color.Black,
                            )
                            Image(
                                painter = painterResource(R.drawable.ic_detail_location),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = eventItem.location,
                            textAlign = TextAlign.Left,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = .6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

            }
        }
    }
}

@Composable
fun TicketToBuy(
    ticketOrder: TicketOrder,
    onQuantityChange: (Int) -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(.7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_detail_ticket),
                contentDescription = null,
                tint = Color(0xFF2C4CA5)
            )
            Spacer(
                modifier = Modifier
                    .width(24.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                // Tipe tiket
                Text(
                    text = ticketOrder.ticketType.name,
                    color = Color.Black.copy(alpha = .6f),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
                // Harga
                Text(
                    text = formatPrice(ticketOrder.ticketType.price),
                    color = Color(0xFF2C4CA5),
                    fontFamily = interBold,
                    fontSize = 18.sp
                )
            }
        }

        QuantitySelector(
            quantity = ticketOrder.quantity,
            maxQuantity = ticketOrder.ticketType.maxQuantity,
            onQuantityChange = onQuantityChange,
            modifier = Modifier.weight(.3f)
        )
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (quantity > 0) {
                    onQuantityChange(quantity - 1)
                }
            },
            enabled = quantity > 0,
            modifier = Modifier
                .alpha(
                    if (quantity > 0) 1f else 0f
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_detailed_enable_min_ticket),
                contentDescription = "Decrease",
            )
        }

        Text(
            text = quantity.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = {
                if (quantity < maxQuantity) {
                    onQuantityChange(quantity + 1)
                }
            },
            enabled = quantity < maxQuantity
        ) {
            Image(
                painter = painterResource(
                    id = if (quantity > 0) R.drawable.ic_detailed_enable_add_ticket
                    else R.drawable.ic_detail_add_ticket),
                contentDescription = "Increase",
            )
        }
    }
}

@Composable
fun DetailOverview(eventItem: EventItem) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Overview",
            fontSize = 20.sp,
            fontFamily = interBold,
            color = Color.Black
        )
        Text(
            text = eventItem.desc,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Justify,
            color = Color.Black.copy(alpha = .6f),
            lineHeight = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun PurchaseSummaryBottom(
    modifier: Modifier,
    totalQuantity: Int,
    totalPrice: Int,
    onPurchaseClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$totalQuantity Tiket",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = formatPrice(totalPrice),
                        fontSize = 18.sp,
                        fontFamily = interBold,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = onPurchaseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedCategoryColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Lanjut Pembayaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// fungsi untuk memformat tanggal
@Composable
fun FormatEventDate(
    startDate : LocalDate,
    endDate : LocalDate,
    modifier: Modifier
) {
    val formatted = remember(startDate, endDate) {
        if (startDate == endDate) {
            buildAnnotatedString {
                appendStyledDayAndMonth(startDate)
            }
        } else if (startDate.month == endDate.month && startDate.year == endDate.year) {
            buildAnnotatedString {
                appendStyledText("${startDate.dayOfMonth}-${endDate.dayOfMonth}", isDay = true)
                append(" ")
                appendStyledText(
                    startDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    isDay = false
                )
            }
        } else {
            buildAnnotatedString {
                appendStyledDayAndMonth(startDate)
                append(" - ")
                appendStyledDayAndMonth(endDate)
            }
        }
    }

    Text(
        text = formatted,
        modifier = modifier,
        color = Color.White,
        textAlign = TextAlign.Left,
        lineHeight = 36.sp

    )
}

// Helper Function
private fun AnnotatedString.Builder.appendStyledText(text:String, isDay:Boolean) {
    withStyle(
        style = SpanStyle(
            fontSize = if (isDay) 36.sp else 24.sp,
            fontWeight = if(isDay) FontWeight.Bold else FontWeight.Medium
        )
    ) {
            append(text)
    }
}

private fun AnnotatedString.Builder.appendStyledDayAndMonth(date: LocalDate) {
    appendStyledText(date.dayOfMonth.toString(), isDay = true)
    append(" ")
    appendStyledText(
        date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        isDay = false
    )
}

// Helper function to calculate distance using Haversine formula
fun calculateDistanceEvent(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0] / 1000 // Convert to kilometers
}

// Helper function to estimate travel time (rough estimation)
fun estimateTravelTimeEvent(distanceKm: Float): String {
    return when {
        distanceKm < 5 -> "${(distanceKm * 10).toInt()} menit"
        distanceKm < 20 -> "${(distanceKm * 8).toInt()} menit"
        distanceKm < 50 -> "${String.format("%.1f", distanceKm / 60 * 45)} jam"
        else -> "${String.format("%.1f", distanceKm / 60)} jam"
    }
}

@Composable
fun GoNowSectionEvent(eventItem: EventItem) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var distance by remember { mutableStateOf<Float?>(null) }
    var isLocationPermissionGranted by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    
    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (isLocationPermissionGranted) {
            isLoadingLocation = true
            getCurrentLocationEvent(fusedLocationClient) { location ->
                userLocation = location
                location?.let {
                    distance = calculateDistanceEvent(
                        it.latitude, it.longitude,
                        eventItem.latitude, eventItem.longtitude
                    )
                }
                isLoadingLocation = false
            }
        }
    }
    
    // Check initial permission
    LaunchedEffect(Unit) {
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || 
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (isLocationPermissionGranted) {
            isLoadingLocation = true
            getCurrentLocationEvent(fusedLocationClient) { location ->
                userLocation = location
                location?.let {
                    distance = calculateDistanceEvent(
                        it.latitude, it.longitude,
                        eventItem.latitude, eventItem.longtitude
                    )
                }
                isLoadingLocation = false
            }
        }
    }
    
    Column {
        Text(
            text = "Navigation",
            fontSize = 20.sp,
            fontFamily = interBold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Distance Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "📍 ${eventItem.location}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        if (isLoadingLocation) {
                            Text(
                                text = "🔄 Mengukur jarak...",
                                fontSize = 12.sp,
                                color = Color(0xFF2C4CA5)
                            )
                        } else if (distance != null) {
                            Text(
                                text = "📏 ${String.format("%.1f", distance)} km dari lokasi Anda",
                                fontSize = 12.sp,
                                color = Color(0xFF2C4CA5),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "⏱️ Estimasi: ${estimateTravelTimeEvent(distance!!)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = if (isLocationPermissionGranted) "❌ Tidak dapat mengakses lokasi" else "📍 Aktifkan lokasi untuk melihat jarak",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Get Location Button
                    if (!isLocationPermissionGranted) {
                        Button(
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2C4CA5)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Aktifkan Lokasi",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    // Go Now Button
                    Button(
                        onClick = {
                            // Open Google Maps with navigation
                            val uri = if (userLocation != null) {
                                // With user location (navigation mode)
                                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${userLocation!!.latitude},${userLocation!!.longitude}&destination=${eventItem.latitude},${eventItem.longtitude}&travelmode=driving")
                            } else {
                                // Without user location (just show destination)
                                Uri.parse("geo:${eventItem.latitude},${eventItem.longtitude}?q=${eventItem.latitude},${eventItem.longtitude}(${Uri.encode(eventItem.title)})")
                            }
                            
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to browser if Google Maps not installed
                                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(browserIntent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedCategoryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Go Now",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// Helper function to get current location
fun getCurrentLocationEvent(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            onLocationReceived(location)
        }.addOnFailureListener {
            onLocationReceived(null)
        }
    } catch (e: SecurityException) {
        onLocationReceived(null)
    }
}
