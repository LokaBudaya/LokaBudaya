package com.dev.lokabudaya.pages.Book

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.smallTextColor
import com.dev.lokabudaya.data.DataProvider

// Main Screen
@Composable
fun BookPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        HeaderSection()
        Spacer(modifier = Modifier.height(16.dp))
        FilterSection()
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
                text = "My Wishlist",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search",
            tint = bigTextColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Filter Section
@Composable
fun FilterSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = "All",
            fontSize = 20.sp,
            color = bigTextColor,
            fontWeight = FontWeight.Medium
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_dropdown),
            contentDescription = "Dropdown",
            tint = bigTextColor,
            modifier = Modifier
                .size(12.dp)
                .padding(start = 4.dp)
        )
    }
}

// Wishlist Section
@Composable
fun WishlistSection() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(DataProvider.wishlistItems) { item ->
            WishlistListItem(item)
            HorizontalDivider(thickness = 2.dp, color = Color(0xFFE0E0E0))
        }
    }
}

// Wishlist Item
@Composable
fun WishlistListItem(item: WishlistItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WishlistImage()
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier.weight(1f)
        ) {
            WishlistContent(item)
        }
        WishlistLoveButton()
    }
}

@Composable
fun WishlistImage() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(80.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_mangkunegaran),
            contentDescription = "Wishlist Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun WishlistContent(item: WishlistItem) {
    Column {
        Text(
            text = item.title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = bigTextColor
        )
        Text(
            text = item.subtitle,
            color = Color.Gray,
            fontSize = 12.sp
        )
        LocationRow(item.location)
        Text(
            text = item.price,
            color = smallTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LocationRow(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location),
            contentDescription = "Location",
            tint = smallTextColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = location,
            color = smallTextColor,
            fontSize = 12.sp
        )
    }
}

@Composable
fun WishlistLoveButton() {
    Icon(
        painter = painterResource(id = R.drawable.ic_love_filled),
        contentDescription = "Love",
        tint = Color.Red,
        modifier = Modifier.size(20.dp)
    )
}

// Data Models
data class WishlistItem(
    val title: String,
    val subtitle: String,
    val location: String,
    val price: String
)