package com.dev.lokabudaya.pages.Home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dev.lokabudaya.R
import kotlinx.coroutines.delay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.ripple
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.categoryColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.mediumTextColor
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.google.firebase.auth.FirebaseAuth
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            is AuthState.EmailNotVerified -> {
                val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                navController.navigate("EmailVerificationPage/$email") {
                    popUpTo("HomePage") { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    HomePageContent(navController, favoriteViewModel)
}

// Top ads section
@Composable
fun TopAdsCarousel(
    modifier: Modifier = Modifier
) {
    val imageList = listOf(
        R.drawable.img_reogponorogo,
        R.drawable.img_event,
        R.drawable.img_reogponorogo
    )

    var currentPage by remember { mutableStateOf(0) }

    var autoSlideEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(autoSlideEnabled, currentPage) {
        if (autoSlideEnabled) {
            delay(5000)
            currentPage = (currentPage + 1) % imageList.size
        }
    }

    val dragState = rememberDraggableState { }
    var dragOffset by remember { mutableStateOf(0f) }
    val dragThreshold = 80f

    Box(
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        autoSlideEnabled = false
                        dragOffset = 0f
                    },
                    onDragEnd = {
                        if (abs(dragOffset) > dragThreshold) {
                            if (dragOffset > 0) {
                                currentPage = (currentPage - 1 + imageList.size) % imageList.size
                            } else {
                                currentPage = (currentPage + 1) % imageList.size
                            }
                        }
                        autoSlideEnabled = true
                        dragOffset = 0f
                    },
                    onDragCancel = {
                        autoSlideEnabled = true
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 500))
            }
        ) { page ->
            val actualPage = page
            val bannerText = when (actualPage) {
                0 -> "Reog\nPonorogo"
                1 -> "Festival Tari Ratoh Jaroe"
                2 -> "Festival Budaya Nusantara"
                else -> ""
            }
            val bannerDateTime = when (bannerText) {
                "Reog\nPonorogo" -> "Solo, 24 Maret 2025\t\t\t\t\t10:00 WIB"
                "Festival Tari Ratoh Jaroe" -> "Bandung, 4 April 2025\t\t\t\t\t08:00 WIB"
                "Festival Budaya Nusantara" -> "Jakarta, 12 Februari 2025\t\t\t\t\t09:00 WIB"
                else -> ""
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(id = imageList[actualPage]),
                        contentDescription = "Top ads banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Image(
                        painter = painterResource(R.drawable.img_gradient),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 44.dp, end = 40.dp)
                ) {
                    Text(
                        text = bannerText,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 48.sp,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontFamily = interBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bannerDateTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(imageList.size) { index ->
                val isSelected = currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                        .clickable {
                            currentPage = index
                            autoSlideEnabled = true
                        }
                )
            }
        }
    }
}

// Category section
@Composable
fun CategoryRow(navController: NavController) {
    val context = LocalContext.current
    val categories = listOf(
        Triple("Kuliner", R.drawable.ic_culinaryhome, Color(0xFF9A5F38)),
        Triple("Wisata", R.drawable.ic_tourhome, Color(0xFF466F79)),
        Triple("Event", R.drawable.ic_eventhome, Color(0xFF76395F))
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categories.forEach { (label, icon, bgColor) ->
            val categoryRoute = when(label) {
                "Kuliner" -> ScreenRoute.Culinary.route
                "Wisata" -> ScreenRoute.Tour.route
                "Event" -> ScreenRoute.Event.route
                else -> ""
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        navController.navigate(categoryRoute)
                    }
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    fontFamily = interBold,
                    color = Color.Black
                )
            }
        }
    }
}

// HomeTab category section
@Composable
fun HomeTab() {
    var selectedCategoryTab by remember { mutableStateOf(0) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(White),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val categoryTab = listOf("Event", "Kuliner", "Wisata")
        categoryTab.forEachIndexed { index, category ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (selectedCategoryTab == index) selectedCategoryColor else Color.Transparent)
                    .clickable { selectedCategoryTab = index }
                    .padding(horizontal = 36.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = if (selectedCategoryTab == index) White else categoryColor,
                    fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedCategoryTab = index }
                )
            }
        }
    }
}

// Current location section
@Composable
fun CurrentLocation(navController: NavController, favoriteViewModel: FavoriteViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Location Icon",
                    tint = bigTextColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "What's in Solo?",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = interBold,
                    color = bigTextColor
                )
            }
            Text(
                text = "Selengkapnya >",
                fontSize = 16.sp,
                color = mediumTextColor,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.navigate(ScreenRoute.Search.route)
                    }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DataProvider.tourItemLists) { place ->
                WhatIsCard(place = place, navController = navController, favoriteViewModel = favoriteViewModel)
            }
        }
    }
}

@Composable
fun WhatIsCard(place: TourItem,
               navController: NavController,
               favoriteViewModel: FavoriteViewModel = viewModel()) {
    var isFav by remember { mutableStateOf(favoriteViewModel.getFavoriteState(place)) }

    Card(
        modifier = Modifier
            .width(168.dp)
            .height(208.dp)
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray
        ),
        onClick = {
            val originalIndex = DataProvider.tourItemLists.indexOf(place)
            navController.navigate("DetailTourPage/$originalIndex")
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = place.imgRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterResource(R.drawable.img_banner),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(0.45f)
                    .offset(y = 16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, top = 96.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = place.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = interBold,
                    color = White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Location Icon",
                        tint = White,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = place.location,
                        color = White,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconToggleButton(checked = isFav, onCheckedChange = {
                        isFav = !isFav
                    }) {
                        Icon(
                            painter = if (isFav)
                                painterResource(R.drawable.ic_love_filled)
                            else
                                painterResource(R.drawable.ic_love_outlined),
                            contentDescription = "Favorite",
                            tint = if (isFav) Color.Red else Color.White,
                            modifier = Modifier
                                .clickable {
                                    favoriteViewModel.toggleFavorite(place)
                                    isFav = favoriteViewModel.getFavoriteState(place)
                                }
                        )
                    }
                }
            }
        }
    }
}

// Recommended section
@Composable
fun Recommended() {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = "Rekomendasi",
            fontFamily = interBold,
            color = bigTextColor,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

// List event section
@Composable
fun ListEvent(navController: NavController) {
    val top3Events = DataProvider.eventItemLists
        .sortedWith(
            compareByDescending<EventItem> { it.rating }
                .thenBy { it.title }
        )
        .take(3)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        top3Events.forEach { event ->
            ListEventCard(event = event, navController = navController)
        }
    }
}

@Composable
fun ListEventCard(event: EventItem, navController: NavController) {
    val formatter = DecimalFormat("#.###")
    val priceFormatted = if (event.price % 1.0 == 0.0) {
        formatter.format(event.price)
    } else {
        event.price.toString()
    }
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(360.dp)
            .height(296.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                val originalIndex = DataProvider.eventItemLists.indexOf(event)
                navController.navigate("DetailEventPage/$originalIndex")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = event.imgRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC222222))
                        )
                    )
            )
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // title
                        Text(
                            text = event.title,
                            color = Color.Black,
                            fontFamily = interBold,
                            fontSize = 24.sp,
                            modifier = Modifier.weight(1f)
                        )
                        // price
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .height(36.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontSize = 14.sp)) {
                                        append("Rp ")
                                    }
                                    withStyle(style = SpanStyle(fontSize = 24.sp)) {
                                        append(NumberFormat.getNumberInstance(Locale("id", "ID")).format(event.price))
                                    }
                                },
                                color = Color(0xFF466F79),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                            )
                            Text(
                                text = "/orang",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationY = 48f
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // star
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier.size(12.dp)
                        )
                        // rating
                        Text(
                            text = event.rating.toString(),
                            color = Color.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 2.dp, end = 6.dp)
                        )
                        // category
                        Text(
                            text = event.label,
                            color = event.textLabelColor,
                            fontSize = 12.sp,
                            fontFamily = interBold,
                            modifier = Modifier
                                .background(event.backgroundLabelColor, RoundedCornerShape(6.dp))
                                .wrapContentSize()
                                .padding(horizontal = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column (
                        ){
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = event.location,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                            ){
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_time),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = event.eventTime,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF9D8C3A))
                                .wrapContentSize()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(radius = 12.dp, color = Color.Black)
                                ) {
                                    val originalIndex = DataProvider.eventItemLists.indexOf(event)
                                    navController.navigate("DetailEventPage/$originalIndex")
                                }
                        ) {
                                Text(
                                    text = "Buy Now",
                                    color = Color.White,
                                    fontFamily = interBold
                                )
                        }
                    }
                }
            }
        }
    }
}

// Blog section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun Blog(navController: NavController) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Blog Journeys",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = interBold,
                color = bigTextColor,
            )
            Text(
                text = "Selengkapnya >",
                fontSize = 16.sp,
                color = mediumTextColor,
                modifier = Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    navController.navigate(ScreenRoute.Blog.route)
                }
            )
        }

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        val cardWidth = 320.dp

        val pagerState = rememberPagerState(pageCount = { DataProvider.blogCards.size })

        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = screenWidth - cardWidth - 16.dp
                ),
                pageSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val blogCard = DataProvider.blogCards[page]
                BlogCard(
                    title = blogCard.title,
                    desc = blogCard.content,
                    imageId = blogCard.imageId
                )
            }
        }
    }
}

@Composable
fun BlogCard(title: String, desc: String, imageId: Int) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .height(240.dp)
            .padding(2.dp, 12.dp)
            .shadow(2.dp, shape = RoundedCornerShape(12.dp), clip = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomEnd
                    )
                    .fillMaxWidth(4f/5f)
                    .fillMaxHeight(2.3f/3f)
            ) {
                Box(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.BottomEnd
                        )
                        .fillMaxWidth(3.2f/4f)
                        .fillMaxHeight(3f/4f)
                        .background(White)
                        .padding(vertical = 12.dp)
                        .padding(start = 36.dp, end = 20.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column {
                        Text(
                            text = if (title.length > 20) title.take(20) + "..." else title,
                            color = Color.Black,
                            fontFamily = poppinsSemiBold,
                            fontSize = 16.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                        )
                        Text(
                            text = if (desc.length > 50) desc.take(50) + "..." else desc,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.TopStart
                        )
                        .size(84.dp)
                        .clip(CircleShape)
                        .border(
                            border = BorderStroke(3.dp, Color.White),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        // painter = painterResource(id = userId.profilepict)
                        painter = painterResource(id = R.drawable.img_people),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, White, shape = RoundedCornerShape(12.dp))
            )
        }

    }
}

// All HomePage content
@Composable
fun HomePageContent(navController: NavController, favoriteViewModel: FavoriteViewModel) {
    LazyColumn(
        modifier = Modifier
            .background(Color(0xFFF8F8F8))
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TopAdsCarousel()
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                CategoryRow(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                CurrentLocation(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                Recommended()
                Spacer(modifier = Modifier.height(16.dp))
                ListEvent(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                Blog(navController = navController)
            }
        }
    }
}