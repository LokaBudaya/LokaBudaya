package com.dev.lokabudaya.pages.Home.Category

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider.tourItemLists
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Search.FilterList
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.bigTextColor
import java.text.DecimalFormat

//Tour Page
@Composable
fun TourPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Column(modifier = modifier.padding(16.dp)) {
        HeaderTourSection(navController)
        Spacer(modifier = Modifier.height(16.dp))
        FilterList()
        Spacer(modifier = Modifier.height(16.dp))
        Screen_Tour()
    }
}

// Header Tour Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderTourSection(navController: NavController) {
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
                text = "Wisata",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
        SearchIcon()
    }
}

@Composable
fun Screen_Tour() {
    _Screen_Tour(tourItemLists = tourItemLists)
}

@Composable
fun _Screen_Tour(tourItemLists: List<TourItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        items(count = tourItemLists.size) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                CreateTour(tourItemLists[it])
            }
        }
    }
}

@Composable
fun CreateTour(tourItem: TourItem) {
    var isFav by remember {
        mutableStateOf(tourItem.isFavorite)
    }
    val formatter = DecimalFormat("#.###")
    val priceFormatted = if (tourItem.price % 1.0 == 0.0) {
        formatter.format(tourItem.price)
    } else {
        tourItem.price.toString()
    }
    Card (
        modifier = Modifier
            .width(164.dp)
            .height(224.dp)
            .shadow(2.dp, shape = RoundedCornerShape(16.dp), clip = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(tourItem.imgRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight(.65f)
                        .fillMaxWidth()
                )
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp),
                ) {
                    Text(
                        text = tourItem.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp$priceFormatted",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff2C4CA5)
                    )
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = null,
                            )
                            Text(
                                text = tourItem.rating.toString(),
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .alpha(.5f)
                            )
                        }
                        Image(
                            painter = if (isFav)
                                painterResource(R.drawable.ic_love_filled)
                            else
                                painterResource(R.drawable.ic_love_outlined),
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .clickable {
                                    isFav = !isFav
                                    tourItem.isFavorite = isFav
                                }
                        )
                    }
                }
            }
        }
    }
}