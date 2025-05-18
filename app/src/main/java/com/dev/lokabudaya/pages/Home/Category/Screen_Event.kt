package com.dev.lokabudaya.pages.Home.Category

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Search.FilterList
import com.dev.lokabudaya.pages.Search.SearchCardItem
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.bigTextColor

//Event Page
@Composable
fun EventPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Column(modifier = modifier.padding(16.dp)) {
        HeaderEventSection(navController)
        Spacer(modifier = Modifier.height(16.dp))
        FilterList()
        Spacer(modifier = Modifier.height(16.dp))
        EventGridList()
    }
}

// Header Event Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderEventSection(navController: NavController) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back Icon",
                tint = bigTextColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        navController.navigate(ScreenRoute.Home.route)
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Event",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
        SearchIcon()
    }
}

@Composable
fun EventGridList() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(DataProvider.eventList.size) { index ->
            val EventItem = DataProvider.eventList[index]
            SearchCardItem(
                image = EventItem.imageRes,
                title = EventItem.title,
                price = EventItem.price,
                rating = EventItem.rating
            )
        }
    }
}