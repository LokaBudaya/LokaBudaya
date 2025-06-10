package com.dev.lokabudaya.pages.Ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor

@Composable
fun TicketListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel
) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        TicketListHeader(
            onBackClick = { navController.popBackStack() }
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = selectedCategoryColor)
            }
        } else if (paidOrders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tiket akan muncul setelah pembayaran berhasil",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    count = paidOrders.size
                ) { index ->
                    CreateTicketFromPaidOrder(
                        orderData = paidOrders[index],
                        onClick = {
                            navController.navigate("DetailTicketFirestore/${paidOrders[index].id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TicketListHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .background(
                    Color.White,
                    CircleShape
                )
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = bigTextColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "My Tickets",
            fontSize = 24.sp,
            fontFamily = interBold,
            color = bigTextColor
        )
    }
}