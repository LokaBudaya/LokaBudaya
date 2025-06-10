package com.dev.lokabudaya.pages.Culinary

import android.content.res.Resources.Theme
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.data.TicketType
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Auth.UserData
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.pages.Home.Category.Event.FormatEventDate
import com.dev.lokabudaya.pages.Ticket.TicketViewModel
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Review
import com.google.firebase.firestore.auth.User
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@Composable
fun DetailCulinaryPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    kulinerItem: KulinerItem,
) {
    val context = LocalContext.current
    val apiKey = stringResource(id = R.string.google_map_api_key)
    val coroutineScope = rememberCoroutineScope()

    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    var isFavorite by remember { mutableStateOf(kulinerItem.isFavorite) }
    LaunchedEffect(favoriteItems) {
        isFavorite = favoriteViewModel.getFavoriteState(kulinerItem)
    }

    val previewImages = listOf(
        kulinerItem.imgRes,
        kulinerItem.imgRes,
        kulinerItem.imgRes
    ).take(3)

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, apiKey)
        }
    }

    val kulinerLocation = LatLng(kulinerItem.latitude, kulinerItem.longtitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kulinerLocation, 15f)
    }

    LaunchedEffect(kulinerLocation) {
        coroutineScope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(kulinerLocation, 16f, 0f, 0f)
                ),
                durationMs = 1500
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        DetailCulinaryItem(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel,
            kulinerItem = kulinerItem
        )
        Column() {
            HorizontalDivider(
                modifier = Modifier.alpha(.2f)
            )

            DetailOverview(kulinerItem)

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.alpha(.2f)
            )

            Column (
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
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
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.alpha(.2f)
            )

            Column (
                modifier = Modifier.padding(16.dp)
            ) {
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
                            state = MarkerState(position = kulinerLocation),
                            title = kulinerItem.title,
                            snippet = kulinerItem.location
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailCulinaryItem(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    kulinerItem: KulinerItem
) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    var isFavorite by remember { mutableStateOf(kulinerItem.isFavorite) }
    LaunchedEffect(favoriteItems) {
        isFavorite = favoriteViewModel.getFavoriteState(kulinerItem)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        Image(
            painter = painterResource(id = kulinerItem.imgRes),
            contentDescription = kulinerItem.title,
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
                favoriteViewModel.toggleFavorite(kulinerItem)
                isFavorite = favoriteViewModel.getFavoriteState(kulinerItem)
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
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.6f),
                    text = kulinerItem.title,
                    fontSize = 48.sp,
                    lineHeight = 48.sp,
                    fontFamily = interBold,
                    color = Color(0xFF01103A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 28.dp)
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(.6f)
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
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rating",
                            fontSize = 12.sp,
                            fontFamily = interBold,
                            color = Color.Black,
                        )
                        Text(
                            text = kulinerItem.rating.toString(),
                            fontSize = 32.sp,
                            fontFamily = interBold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

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
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "People Reviews",
                            fontSize = 12.sp,
                            fontFamily = interBold,
                            color = Color.Black,
                        )
                        PeopleReviews()
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Card(
                    modifier = Modifier
                        .weight(.9f)
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
                            .padding(top = 4.dp, bottom = 12.dp)
                            .fillMaxWidth(),
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
                            text = kulinerItem.kulinerTime,
                            fontSize = 12.sp,
                            fontFamily = interBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PeopleReviews(
    // bantu gas
    //reviewer: List<UserData>
) {
    val maxVisible = 3
    val avatarSize = 40.dp
    val overlap = (-24).dp
    var count = 0

    val displayedAvatars = reviewerDummy.take(maxVisible)
    val extraCount = reviewerDummy.size - maxVisible

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(overlap)
        ) {
            displayedAvatars.forEach() { reviewer ->
                count++
                Image(
                    painter = painterResource(
                        if (true) { // pengkondisian klo ada profile atau gak
                            reviewer
                        } else {
                            R.drawable.ic_default_profile
                        }
                    ),
                    contentDescription = "Reviewer avatar",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(Color(0xFFC4C4C4))
                        .border(2.dp, Color(0xFF2C4CA5), CircleShape),
                    contentScale = ContentScale.Crop,
                )
                if (count >= 3 && reviewerDummy.size > 3) {
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .background(Color(0xFF2C4CA5))
                            .border(2.dp, Color(0xFF2C4CA5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$extraCount",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = interBold
                        )
                    }
                }
            }
        }
    }
}

val reviewerDummy = listOf(
    R.drawable.img_people,
    R.drawable.ic_default_profile,
    R.drawable.img_event,
    R.drawable.ic_default_profile,
    R.drawable.ic_default_profile,
    R.drawable.ic_default_profile,
    R.drawable.ic_default_profile,
    R.drawable.ic_default_profile
)

@Composable
@Preview
fun Preview() {
    LokaBudayaTheme {
        val culinaryItem = DataProvider.kulinerItemLists[2]
//        DetailCulinaryItem(
//            modifier = Modifier,culinaryItem
//        )
    }
}


@Composable
fun DetailOverview(kulinerItem: KulinerItem) {
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
            text = kulinerItem.desc,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Justify,
            color = Color.Black.copy(alpha = .6f),
            lineHeight = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}