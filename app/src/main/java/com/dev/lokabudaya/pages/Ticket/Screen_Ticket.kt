package com.dev.lokabudaya.pages.Ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.pages.Book.WishlistSection
import com.dev.lokabudaya.pages.Home.RecommendedCard
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.mediumTextColor

// Main Screen
@Composable
fun TicketPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        HeaderSection()
        Spacer(modifier = Modifier.height(16.dp))
        TicketList()
        Spacer(modifier = Modifier.height(16.dp))
        WishlistHeader()
        WishlistSection()
    }
}

// Header Section
@Composable
fun HeaderSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "My Orders",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
            )
        }
        SearchIcon()
    }
}

// Ticket section
@Composable
fun TicketList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_ticket),
            contentDescription = "Ticket image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}

// Wishlist section
@Composable
fun WishlistHeader() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = "Your Wishlist",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = bigTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Selengkapnya >",
            fontSize = 12.sp,
            color = mediumTextColor,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
    }
}

// Search Icon Component
@Composable
fun SearchIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_search),
        contentDescription = "Search",
        tint = bigTextColor,
        modifier = Modifier.size(20.dp)
    )
}