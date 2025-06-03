package com.dev.lokabudaya.pages.Ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider.ticketItemLists
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.bigTextColor

@Composable
fun TicketListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // Header dengan back button
        TicketListHeader(
            onBackClick = { navController.popBackStack() }
        )

        // List semua tiket
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                count = ticketItemLists.size
            ) { index ->
                CreateTicket(
                    ticketItemLists[index],
                    onClick = {
                        val originalIndex = ticketItemLists.indexOf(ticketItemLists[index])
                        navController.navigate("DetailTicketPage/$originalIndex")
                    }
                )
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
        // Back button
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

        // Title
        Text(
            text = "My Tickets",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = bigTextColor
        )
    }
}