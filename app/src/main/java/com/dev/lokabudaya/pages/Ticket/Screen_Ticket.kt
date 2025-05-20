package com.dev.lokabudaya.pages.Ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.DataProvider.myTickets
import com.dev.lokabudaya.data.Ticket
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.WishlistListItem
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.mediumTextColor

// Main Screen
@Composable
fun TicketPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            HeaderSection()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            CreateTicket(myTickets[0])
            Spacer(modifier = Modifier.height(16.dp))
            CreateTicket(myTickets[0])
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            WishlistHeader(navController)
            WishlistSectionTicket()
        }
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
fun CreateTicket(ticket: Ticket) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(204.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_ticket),
            contentDescription = "Ticket image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 17.dp)
                .padding(top = 16.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.58f),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    textAlign = TextAlign.Left,
                    text = ticket.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 44.sp,
                    lineHeight = 44.sp,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column (
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Column (
                        horizontalAlignment = Alignment.End
                    ){
                        Text(
                            textAlign = TextAlign.Right,
                            text = ticket.date,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            textAlign = TextAlign.Right,
                            text = ticket.location,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = "Lihat Detail",
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.42f)
                    .drawBehind {
                        val borderSize = 2.dp.toPx()
                        val y = borderSize/2

                        drawLine(
                            color = Color.White,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = borderSize,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(borderSize * 4, borderSize * 4),
                                phase = 0f
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(ticket.qrCode),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    LokaBudayaTheme {
        CreateTicket(ticket = myTickets[0])
    }
}

// Wishlist section
@Composable
fun WishlistHeader(navController: NavController) {
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
                .clickable {
                    navController.navigate(ScreenRoute.Book.route)
                }
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

// Wishlist Ticket Section
@Composable
fun WishlistSectionTicket() {
    val topThreeItems = DataProvider.wishlistItems.take(3)

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        topThreeItems.forEach { item ->
            WishlistListItem(item)
            HorizontalDivider(thickness = 2.dp, color = Color(0xFFE0E0E0))
        }
    }
}
