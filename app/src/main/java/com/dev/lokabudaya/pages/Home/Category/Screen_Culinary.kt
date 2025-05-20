package com.dev.lokabudaya.pages.Search.Kuliner

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider.kulinerItemLists
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.pages.Search.FilterList
import java.text.DecimalFormat

//Culinary Page
@Composable
fun CulinaryPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Column(modifier = modifier.padding(16.dp)) {
        HeaderCulinarySection(navController)
        Spacer(modifier = Modifier.height(16.dp))
        FilterList()
        Spacer(modifier = Modifier.height(16.dp))
        Screen_Kuliner()
    }
}

// Header Culinary Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderCulinarySection(navController: NavController) {
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
                text = "Kuliner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
        SearchIcon()
    }
}

@Composable
fun Screen_Kuliner() {
    _Screen_Kuliner(kulinerItemLists = kulinerItemLists)
}

@Composable
fun _Screen_Kuliner(kulinerItemLists: List<KulinerItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        items(count = kulinerItemLists.size) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                CreateKuliner(kulinerItemLists[it])
            }
        }
    }
}

@Composable
fun CreateKuliner(kulinerItem: KulinerItem) {
    var isFav by remember {
        mutableStateOf(kulinerItem.isFavorite)
    }
    val formatter = DecimalFormat("#.###")
    val priceFormatted = if (kulinerItem.price % 1.0 == 0.0) {
        formatter.format(kulinerItem.price)
    } else {
        kulinerItem.price.toString()
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
                    painter = painterResource(kulinerItem.imgRes),
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
                        text = kulinerItem.title,
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
                                text = kulinerItem.rating.toString(),
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
                                    kulinerItem.isFavorite = isFav
                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Screen_Kuliner_Preview() {
    LokaBudayaTheme {
//        CreateKuliner(kuliner_item)
        Screen_Kuliner()
    }
}

