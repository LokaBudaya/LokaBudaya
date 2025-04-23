package com.dev.lokabudaya.pages

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.ui.theme.White

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
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }
    }
    Column(
        modifier
            .defaultMinSize(minHeight = 300.dp)
            .fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(id = imageList[actualPage]),
                    contentDescription = "Top ads banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 24.dp, end = 40.dp)
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
    }
}

// Recommended section
@Composable
fun Recommended() {
    Text(
        text = "Recommended",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 92.dp, bottom = 8.dp, start = 12.dp)
    )
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
    val imageRes = when (title) {
        "Mangkunegaran" -> R.drawable.img_mangkunegaran
        "Candi Borobudur" -> R.drawable.img_borobudur
        "Pasar Gede" -> R.drawable.img_pasargede
        else -> R.drawable.img_banner
    }
    val locationRes = when (title) {
        "Mangkunegaran" -> "Surakarta"
        "Candi Borobudur" -> "Yogyakarta"
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
                    .padding(start = 12.dp, top = 60.dp),
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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_love),
                        contentDescription = "Love Icon",
                        tint = White,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(20.dp)
                            .offset(y = 12.dp)
                    )
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
                .padding(end = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Location Icon",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "What's in Solo?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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
        DataProvider.eventItems.forEach { item ->
            ListEventCard(title = item)
        }
    }
}

@Composable
fun ListEventCard(title: String) {
    val imageRes = when (title) {
        "Wayang Kulit" -> R.drawable.img_event
        "Pencak Silat" -> R.drawable.img_event
        "Jajar Festival" -> R.drawable.img_event
        else -> R.drawable.img_event
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(360.dp)
            .height(296.dp),
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
                Recommended()
                Spacer(modifier = Modifier.height(16.dp))
                CurrentLocation()
                Spacer(modifier = Modifier.height(16.dp))
                ListEvent()
            }
        }
    }
}