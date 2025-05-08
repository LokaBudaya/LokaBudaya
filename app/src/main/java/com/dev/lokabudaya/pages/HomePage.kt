package com.dev.lokabudaya.pages

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.categoryColor
import com.dev.lokabudaya.ui.theme.mediumTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor

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
        R.drawable.img_reogponorogo,
        R.drawable.img_reogponorogo
    )
    val virtualPageCount = 3000
    val startIndex = virtualPageCount / 2
    val pagerState = rememberPagerState(pageCount = { virtualPageCount })
    
    LaunchedEffect(Unit) {
        pagerState.animateScrollToPage(startIndex)
    }
    
    LaunchedEffect(pagerState) {
        delay(1000)
        while (true) {
            delay(5000)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    animationSpec = tween(durationMillis = 350)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 300.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val actualPage = page % imageList.size
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
                Image(
                    painter = painterResource(id = imageList[actualPage]),
                    contentDescription = "Top ads banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painterResource(R.drawable.img_gradient),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(120.dp)
                        .offset(y = 10.dp)
                )
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
                val isSelected = (pagerState.currentPage % imageList.size == index)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 8.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                        )
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
            modifier = Modifier.fillMaxSize()
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
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC222222))
                        )
                    )
            )
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = event.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = event.rating.toString(),
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 2.dp, end = 6.dp)
                        )
                        Text(
                            text = event.category,
                            color = Color(0xFF3B82F6),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(Color(0xFFEEF6FF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = event.price,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = event.time,
                            color = Color.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3B82F6))
                                .clickable { }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
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
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = "Blog Journeys",
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
            BlogCard(title = item)
        }
    }
}

@Composable
fun BlogCard(title: String) {
    val imageRes = when (title) {
        "Blog 1" -> R.drawable.img_people
        "Blog 2" -> R.drawable.img_people
        "Blog 3" -> R.drawable.img_people
        else -> R.drawable.img_people
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(296.dp)
            .height(232.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
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