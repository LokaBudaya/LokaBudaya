package com.dev.lokabudaya.pages

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dev.lokabudaya.R
import kotlinx.coroutines.delay

@Composable
fun HomePage(modifier: Modifier) {
    TopAdsCarousel()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopAdsCarousel(
    modifier: Modifier = Modifier
) {
    val imageList = listOf(
        R.drawable.ic_banner,
        R.drawable.ic_banner_2,
        R.drawable.ic_banner_3
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
            Image(
                painter = painterResource(id = imageList[actualPage]),
                contentDescription = "Top ads banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}