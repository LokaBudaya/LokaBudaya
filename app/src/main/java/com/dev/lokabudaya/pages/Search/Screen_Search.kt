package com.dev.lokabudaya.pages.Search

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor

// Main Screen
@Composable
fun SearchPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection()
        SearchBarSection(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        FilterList()
        ExploreGridList()
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
                text = "Explore",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
            )
        }
    }
}

// Search Bar Section
@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        modifier = Modifier.fillMaxWidth()
    )
}

// Search Bar Component
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Search Your Destination..",
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Black
            )
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}

// Filter section
@Composable
fun FilterList() {
    Icon(
        painter = painterResource(id = R.drawable.ic_filter),
        contentDescription = "Filter",
        tint = bigTextColor,
        modifier = Modifier.size(20.dp)
    )
}

// Explore content list
@Composable
fun ExploreGridList() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(DataProvider.eventList.size) { index ->
            val eventItem = DataProvider.eventList[index]
            CulinaryCardItem(
                image = eventItem.imageRes,
                title = eventItem.title,
                price = eventItem.price,
                rating = eventItem.rating
            )
        }
    }
}

@Composable
fun CulinaryCardItem(
    image: Int,
    title: String,
    price: String,
    rating: Double,
    modifier: Modifier = Modifier
) {
    val checkLove = remember { mutableStateOf(true) }
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .width(152.dp)
            .height(236.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .size(100.dp)
            )

            Text(
                text = title,
                fontSize = 14.sp,
                lineHeight = 2.sp,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Rp ${price}",
                color = selectedCategoryColor,
                fontSize = 16.sp,
                lineHeight = 2.sp,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = null,
                        tint = Color(0xFFFFCC00),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${rating}",
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                IconToggleButton(
                    checked = checkLove.value,
                    onCheckedChange = {
                        checkLove.value = !checkLove.value
                    }
                ) {
                    Icon(
                        painter = if (checkLove.value) {
                            painterResource(id = R.drawable.ic_love)
                        } else {
                            painterResource(id = R.drawable.ic_love_filled)
                        },
                        contentDescription = "Love Icon",
                        tint = if (checkLove.value) Color.Black else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}