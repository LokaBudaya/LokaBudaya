package com.dev.lokabudaya.pages.Payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.dev.lokabudaya.data.PaymentTicketOrder
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.dev.lokabudaya.pages.Ticket.TicketViewModel
import com.dev.lokabudaya.pages.Ticket.formatEventDateTimeRange
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

@Composable
fun MidtransPaymentPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    ticketViewModel: TicketViewModel = viewModel()
) {
    val ticketOrdersList: List<PaymentTicketOrder> by ticketViewModel.selectedTicketOrders.collectAsState()
    val eventItem by ticketViewModel.selectedEventItem.collectAsState()

    if (eventItem == null || ticketOrdersList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = selectedCategoryColor)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading payment data...")
            }
        }

        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            if (eventItem == null || ticketOrdersList.isEmpty()) {
                navController.popBackStack()
            }
        }
        return
    }

    val nonNullEventItem = eventItem!!
    val context = LocalContext.current
    val totalAmount = ticketOrdersList.sumOf { order: PaymentTicketOrder -> order.totalPrice }
    val totalQuantity = ticketOrdersList.sumOf { order: PaymentTicketOrder -> order.quantity }
    val filteredTicketOrders = ticketOrdersList.filter { order: PaymentTicketOrder -> order.quantity > 0 }

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // Header
        PaymentHeader(
            eventItem = nonNullEventItem,
            onBackClick = { navController.popBackStack() }
        )

        // Content
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Summary
            item {
                EventSummaryCard(eventItem = nonNullEventItem)
            }

            // Ticket Details
            item {
                Text(
                    text = "Detail Tiket",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            items(filteredTicketOrders) { order ->
                TicketOrderCard(ticketOrder = order)
            }

            // Payment Summary
            item {
                PaymentSummaryCard(
                    totalQuantity = totalQuantity,
                    totalAmount = totalAmount
                )
            }

            // Spacer untuk bottom button
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom Payment Button
        PaymentBottomSection(
            totalAmount = totalAmount,
            isLoading = isLoading,
            onPaymentClick = {
                isLoading = true
                processPayment(
                    context = context,
                    eventItem = nonNullEventItem,
                    ticketOrders = ticketOrdersList,
                    totalAmount = totalAmount,
                    navController = navController,
                    ticketViewModel = ticketViewModel,
                    onComplete = { isLoading = false }
                )
            }
        )
    }
}

@Composable
fun PaymentHeader(
    eventItem: EventItem,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = eventItem.imgRes),
            contentDescription = eventItem.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Title
        Text(
            text = "Pembayaran",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

fun formatEventDateTime(startDate: LocalDate, eventTime: String): String {
    // Format tanggal ke Bahasa Indonesia
    val dayOfMonth = startDate.dayOfMonth
    val month = when (startDate.monthValue) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> "Unknown"
    }
    val year = startDate.year

    return "$dayOfMonth $month $year, $eventTime"
}

@Composable
fun EventSummaryCard(eventItem: EventItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = eventItem.imgRes),
                contentDescription = eventItem.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = eventItem.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = eventItem.location,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = formatEventDateTimeRange(
                        startDate = eventItem.startDate,
                        endDate = eventItem.endDate,
                        eventTime = eventItem.eventTime
                    ),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TicketOrderCard(ticketOrder: PaymentTicketOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = ticketOrder.ticketTypeName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${ticketOrder.quantity} x ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(ticketOrder.price)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(ticketOrder.totalPrice),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = selectedCategoryColor
            )
        }
    }
}

@Composable
fun PaymentSummaryCard(
    totalQuantity: Int,
    totalAmount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ringkasan Pembayaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Tiket ($totalQuantity)",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalAmount),
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Pembayaran",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalAmount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = selectedCategoryColor
                )
            }
        }
    }
}

@Composable
fun PaymentBottomSection(
    totalAmount: Int,
    isLoading: Boolean,
    onPaymentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Pembayaran",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalAmount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = onPaymentClick,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedCategoryColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Bayar Sekarang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Function untuk process payment dengan Midtrans
fun processPayment(
    context: android.content.Context,
    eventItem: EventItem,
    ticketOrders: List<PaymentTicketOrder>,
    totalAmount: Int,
    navController: NavController,
    ticketViewModel: TicketViewModel,
    onComplete: () -> Unit
) {
    try {
        // TODO: Implement Midtrans payment integration
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            ticketViewModel.saveTicketAfterPaymentEvent(
                eventItem = eventItem,
                ticketOrders = ticketOrders,
                totalAmount = totalAmount,
                onSuccess = {
                    android.util.Log.d("Payment", "Ticket saved successfully")
                    onComplete() // Reset loading state
                    navController.navigate("PaymentSuccessPage") {
                        // PERBAIKAN: Clear back stack untuk prevent back to payment
                        popUpTo("MidtransPaymentPage") { inclusive = true }
                    }
                },
                onError = { error ->
                    android.util.Log.e("Payment", "Failed to save ticket: ${error.message}")
                    onComplete() // Reset loading state
                    // TODO: Show error message to user
                }
            )
        }, 2000)
    } catch (e: Exception) {
        android.util.Log.e("Payment", "Process payment error: ${e.message}")
        onComplete()
        // TODO: Show error message
    }
}