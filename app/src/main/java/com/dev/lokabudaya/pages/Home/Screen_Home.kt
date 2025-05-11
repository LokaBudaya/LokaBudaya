package com.dev.lokabudaya.pages.Home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.categoryColor
import com.dev.lokabudaya.ui.theme.mediumTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import kotlin.math.abs

@Composable
fun HomePage(modifier: Modifier) {
    HomePageContent()
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
                        fontWeight = FontWeight.Bold
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

// Recommended section
@Composable
fun Recommended() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = "Recommended",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = bigTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Selengkapnya >",
            fontSize = 16.sp,
            color = mediumTextColor,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(DataProvider.recommendedItems) { item ->
            RecommendedCard(title = item)
        }
    }
}

@Composable
fun RecommendedCard(title: String) {
    val checkLove = remember { mutableStateOf(true) }
    val imageRes = when (title) {
        "Mangkunegaran" -> R.drawable.img_mangkunegaran
        "Candi Borobudur" -> R.drawable.img_borobudur
        "Pasar Gede" -> R.drawable.img_pasargede
        else -> R.drawable.img_banner
    }
    val locationRes = when (title) {
        "Mangkunegaran" -> "Surakarta"
        "Candi Borobudur" -> "Magelang"
        "Pasar Gede" -> "Surakarta"
        else -> ""
    }
    Card(
        modifier = Modifier
            .width(168.dp)
            .height(208.dp)
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray
        ),
        onClick = { },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageRes),
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
                        text = locationRes,
                        color = White,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconToggleButton(checked = checkLove.value, onCheckedChange = {
                        checkLove.value = !checkLove.value
                    }) {
                        Icon(
                            painter = if (checkLove.value) {
                                painterResource(id = R.drawable.ic_love)
                            } else {
                                painterResource(id = R.drawable.ic_love_filled)
                            },
                            contentDescription = "Love Icon",
                            tint = if (checkLove.value) Color.White else Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// Current location section
@Composable
fun CurrentLocation() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterEnd)
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
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
    }
}

// List event section
@Composable
fun ListEvent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DataProvider.eventList.forEach { event ->
            ListEventCard(event = event)
        }
    }
}

@Composable
fun ListEventCard(event: EventItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(360.dp)
            .height(296.dp),
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
                painter = painterResource(id = event.imageRes),
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
                            fontWeight = FontWeight.Bold,
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
                                text = "Rp. ${event.price}",
                                color = Color(0xFF2C4CA5),
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
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
                            text = event.category,
                            color = Color(0xFF00B6EA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color(0xFFC3F2FF), RoundedCornerShape(6.dp))
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
                                    text = event.time,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF9D8C3A))
                                .wrapContentSize()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .clickable { }
                        ) {
                            Text(
                                text = "Buy Now",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// Blog section
@Composable
fun Blog() {
    Column { Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = "Blog Journeys",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = bigTextColor,
        )
        Text(
            text = "Selengkapnya >",
            fontSize = 16.sp,
            color = mediumTextColor
        )
    }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(DataProvider.blogCards) { blogCard ->
                BlogCard(title = blogCard.title, desc = blogCard.desc, imageId = blogCard.imageId)
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
                            text = title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                        )
                        Text(
//                            text = desc,
                            text = desc,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.Light,
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


@Composable
@Preview
fun PreviewListEvent() {
    LokaBudayaTheme {
        Blog()
    }
}

// All HomePage content
@Composable
fun HomePageContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
                HomeTab()
                Spacer(modifier = Modifier.height(16.dp))
                Recommended()
                Spacer(modifier = Modifier.height(16.dp))
                CurrentLocation()
                Spacer(modifier = Modifier.height(16.dp))
                ListEvent()
                Spacer(modifier = Modifier.height(16.dp))
                Blog()
            }
        }
    }
}

data class EventItem(
    val title: String,
    val imageRes: Int,
    val rating: Double,
    val category: String,
    val location: String,
    val time: String,
    val price: String
)

data class BlogCardClass(
    // val userId : UserId //object UserId -> ada profile pict, sama username
    val title: String,
    val desc: String,
    val imageId: Int
)