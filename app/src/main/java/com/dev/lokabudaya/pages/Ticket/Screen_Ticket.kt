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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.OrderData
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.pages.Book.WishlistListItem
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.mediumTextColor
import com.dev.lokabudaya.ui.theme.poppinsLight
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor

// Main Screen
@Composable
fun TicketPage(modifier: Modifier = Modifier,
               navController: NavController,
               authViewModel: AuthViewModel,
               ticketViewModel: TicketViewModel
) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val authState = authViewModel.authState.observeAsState()
    val userTickets by ticketViewModel.userTickets.collectAsState()
    val paidOrders by ticketViewModel.paidOrders.collectAsState()
    val isLoading by ticketViewModel.isLoading.collectAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            is AuthState.Authenticated -> {
                ticketViewModel.refreshTickets()
            }
            else -> Unit
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            HeaderSection(navController = navController)
        }
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Recent Orders",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = interBold,
                    fontSize = 18.sp,
                    color = bigTextColor
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = selectedCategoryColor)
                }
            }
        } else if (paidOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum ada tiket",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            val topThreeOrders = paidOrders.take(3)
            items(
                count = topThreeOrders.size
            ) { index ->
                Spacer(modifier = Modifier.height(16.dp))
                CreateTicketFromPaidOrder(
                    orderData = topThreeOrders[index],
                    onClick = {
                        navController.navigate("DetailTicketFirestore/${topThreeOrders[index].id}")
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            WishlistHeader(navController)
            WishlistSectionTicket(favoriteViewModel, navController = navController)
        }
    }
}

@Composable
fun CreateTicketFromPaidOrder(
    orderData: OrderData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(204.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_ticket),
            contentDescription = "Ticket image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 17.dp)
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.58f),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    textAlign = TextAlign.Left,
                    text = orderData.eventTitle,
                    fontFamily = poppinsSemiBold,
                    fontSize = 44.sp,
                    lineHeight = 44.sp,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            textAlign = TextAlign.Right,
                            text = formatTicketDate(orderData.eventStartDate, orderData.eventTime),
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            textAlign = TextAlign.Right,
                            text = orderData.eventLocation,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lihat Detail",
                        textAlign = TextAlign.Right,
                        fontFamily = poppinsLight,
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
                        val y = borderSize / 2

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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = orderData.orderId.take(12),
                        fontFamily = FontFamily(Font(R.font.libre_barcode_128)),
                        color = White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 84.sp
                    )
                }
            }
        }
    }
}

fun formatTicketDate(dateString: String, timeString: String): String {
    return try {
        val date = java.time.LocalDate.parse(dateString)
        val day = date.dayOfMonth
        val month = when (date.monthValue) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "Mei"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Agu"
            9 -> "Sep"
            10 -> "Okt"
            11 -> "Nov"
            12 -> "Des"
            else -> "Unknown"
        }
        "$day $month"
    } catch (e: Exception) {
        dateString
    }
}

// Header Section
@Composable
fun HeaderSection(navController: NavController) {
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
                    fontFamily = interBold,
                    color = bigTextColor
                )
            )
        }
        TicketDetailIcon(navController = navController)
    }
}

@Composable
fun SearchIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_search),
        contentDescription = "Search",
        tint = bigTextColor,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
fun TicketDetailIcon(navController: NavController) {
    Icon(
        painter = painterResource(id = R.drawable.ic_ticketdetail),
        contentDescription = "Detail Ticket",
        tint = bigTextColor,
        modifier = Modifier
            .size(28.dp)
            .clickable {
                navController.navigate("TicketListPage")
            }
    )
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
            fontFamily = interBold,
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

// Wishlist Ticket Section
@Composable
fun WishlistSectionTicket(favoriteViewModel: FavoriteViewModel, navController: NavController) {
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    val allFavoriteItems by remember(favoriteItems) {
        derivedStateOf { favoriteViewModel.getAllFavoriteItems() }
    }
    val topThreeItems by remember(allFavoriteItems) {
        derivedStateOf { allFavoriteItems.take(3) }
    }

    if (topThreeItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tidak ada wishlist saat ini.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            topThreeItems.forEachIndexed { index, item ->
                key(favoriteViewModel.getItemId(item)) {
                    WishlistListItem(
                        item = item,
                        favoriteViewModel = favoriteViewModel,
                        navController = navController,
                        onFavoriteChanged = {
                        }
                    )
                }
                if (index < topThreeItems.size - 1) {
                    HorizontalDivider(thickness = 2.dp, color = Color(0xFFE0E0E0))
                }
            }
        }
    }
}