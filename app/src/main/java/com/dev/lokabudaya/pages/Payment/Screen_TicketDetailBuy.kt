package com.dev.lokabudaya.pages.Ticket

import NetworkImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.TicketOrder
import com.dev.lokabudaya.data.TicketType
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

@Composable
fun TicketDetailBuyPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    eventItem: EventItem,
    ticketViewModel: TicketViewModel = viewModel()
) {
    val ticketTypes = remember {
        listOf(
            TicketType(
                id = "dewasa",
                name = "Tiket Dewasa",
                price = eventItem.price,
                description = "Tiket khusus dewasa di atas 17 tahun",
                maxQuantity = 10
            ),
            TicketType(
                id = "anak",
                name = "Tiket Anak",
                price = eventItem.price - 10000,
                description = "Tiket khusus anak di bawah 17 tahun",
                maxQuantity = 10
            )
        )
    }

    var ticketOrders by remember {
        mutableStateOf(ticketTypes.map { TicketOrder(it, 0) })
    }

    val totalQuantity = ticketOrders.sumOf { it.quantity }
    val totalPrice = ticketOrders.sumOf { it.totalPrice }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        TicketDetailHeader(
            eventItem = eventItem,
            onBackClick = { navController.popBackStack() }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EventInfoSection(eventItem)
            }

            item {
                Text(
                    text = "Pilih Tiket",
                    fontSize = 20.sp,
                    fontFamily = interBold,
                    color = Color.Black
                )
            }

            items(ticketOrders) { ticketOrder ->
                TicketSelectionCard(
                    ticketOrder = ticketOrder,
                    onQuantityChange = { newQuantity ->
                        ticketOrders = ticketOrders.map { order ->
                            if (order.ticketType.id == ticketOrder.ticketType.id) {
                                order.copy(quantity = newQuantity)
                            } else {
                                order
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (totalQuantity > 0) {
            PurchaseSummaryBottom(
                totalQuantity = totalQuantity,
                totalPrice = totalPrice,
                onPurchaseClick = {
                    ticketViewModel.updateTicketOrdersEvent(ticketOrders, eventItem)
                    navController.navigate("MidtransPaymentPage")
                }
            )
        }
    }
}

@Composable
fun TicketDetailHeader(
    eventItem: EventItem,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        NetworkImage(
            imageUrl = eventItem.imageUrl,
            fallbackRes = eventItem.imgRes,
            contentDescription = eventItem.title,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.5f)
                        ),
                        startY = 100f
                    )
                )
        )

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

        Text(
            text = eventItem.title,
            fontSize = 24.sp,
            fontFamily = interBold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Composable
fun EventInfoSection(eventItem: EventItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_time),
                    contentDescription = "Date",
                    tint = selectedCategoryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatEventDateTimeRange(
                        startDate = eventItem.startDate,
                        endDate = eventItem.endDate,
                        eventTime = eventItem.eventTime
                    ),
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = selectedCategoryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = eventItem.location,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
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

fun formatEventDateTimeRange(startDate: LocalDate, endDate: LocalDate, eventTime: String): String {
    return if (startDate == endDate) {
        // Single day event
        formatEventDateTime(startDate, eventTime)
    } else {
        // Multi-day event
        val startFormatted = formatEventDateTime(startDate, "")
        val endFormatted = formatEventDateTime(endDate, "")
        "$startFormatted - $endFormatted, $eventTime"
    }
}

@Composable
fun TicketSelectionCard(
    ticketOrder: TicketOrder,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ticketOrder.ticketType.name,
                        fontSize = 16.sp,
                        fontFamily = interBold,
                        color = Color.Black
                    )
                    Text(
                        text = ticketOrder.ticketType.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            .format(ticketOrder.ticketType.price),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = selectedCategoryColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                QuantitySelector(
                    quantity = ticketOrder.quantity,
                    maxQuantity = ticketOrder.ticketType.maxQuantity,
                    onQuantityChange = onQuantityChange
                )
            }

            if (ticketOrder.quantity > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subtotal: ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(ticketOrder.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = {
                if (quantity > 0) {
                    onQuantityChange(quantity - 1)
                }
            },
            enabled = quantity > 0,
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (quantity > 0) selectedCategoryColor else Color.Gray.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_min),
                contentDescription = "Decrease",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = quantity.toString(),
            fontSize = 18.sp,
            fontFamily = interBold,
            color = Color.Black,
            modifier = Modifier.widthIn(min = 24.dp),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = {
                if (quantity < maxQuantity) {
                    onQuantityChange(quantity + 1)
                }
            },
            enabled = quantity < maxQuantity,
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (quantity < maxQuantity) selectedCategoryColor else Color.Gray.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PurchaseSummaryBottom(
    totalQuantity: Int,
    totalPrice: Int,
    onPurchaseClick: () -> Unit
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
                        text = "$totalQuantity Tiket",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            .format(totalPrice),
                        fontSize = 18.sp,
                        fontFamily = interBold,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = onPurchaseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedCategoryColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Lanjut Pembayaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}