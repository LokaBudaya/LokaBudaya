package com.dev.lokabudaya.pages.Payment

import android.util.Log
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
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.CustomerDetail
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.ItemDetail
import com.dev.lokabudaya.data.MidtransRequest
import com.dev.lokabudaya.data.MidtransResponse
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.network.MidtransAPI
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.dev.lokabudaya.pages.Ticket.TicketViewModel
import com.dev.lokabudaya.pages.Ticket.formatEventDateTimeRange
import com.dev.lokabudaya.ui.theme.interBold
import com.google.firebase.auth.FirebaseAuth
import com.midtrans.sdk.corekit.core.MidtransSDK
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

@Composable
fun MidtransPaymentPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    ticketViewModel: TicketViewModel
) {
    val ticketOrdersList: List<PaymentTicketOrder> by ticketViewModel.selectedTicketOrders.collectAsState()
    val eventItem by ticketViewModel.selectedEventItem.collectAsState()
    val tourItem by ticketViewModel.selectedTourItem.collectAsState()

    if ((eventItem == null && tourItem == null) || ticketOrdersList.isEmpty()) {
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

                Text(
                    text = "Event: ${eventItem?.title ?: "null"}, Tour: ${tourItem?.title ?: "null"}, Orders: ${ticketOrdersList.size}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        return
    }

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
        PaymentHeader(
            eventItem = eventItem,
            tourItem = tourItem,
            onBackClick = { navController.popBackStack() }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (eventItem != null) {
                    EventSummaryCard(eventItem = eventItem!!)
                } else if (tourItem != null) {
                    TourSummaryCard(tourItem = tourItem!!)
                }
            }

            item {
                Text(
                    text = "Detail Tiket",
                    fontSize = 18.sp,
                    fontFamily = interBold,
                    color = Color.Black
                )
            }

            items(filteredTicketOrders) { order ->
                TicketOrderCard(ticketOrder = order)
            }

            item {
                PaymentSummaryCard(
                    totalQuantity = totalQuantity,
                    totalAmount = totalAmount
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        PaymentBottomSection(
            totalAmount = totalAmount,
            isLoading = isLoading,
            onPaymentClick = {
                isLoading = true
                processPayment(
                    context = context,
                    eventItem = eventItem,
                    tourItem = tourItem,
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
    eventItem: EventItem?,
    tourItem: TourItem?,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = eventItem?.imgRes ?: tourItem?.imgRes ?: R.drawable.img_event),
            contentDescription = eventItem?.title ?: tourItem?.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
            text = "Pembayaran",
            fontSize = 24.sp,
            fontFamily = interBold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

fun formatEventDateTime(startDate: LocalDate, eventTime: String): String {
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
                    fontFamily = interBold,
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
fun TourSummaryCard(tourItem: TourItem) {
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
                painter = painterResource(id = tourItem.imgRes),
                contentDescription = tourItem.title,
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
                    text = tourItem.title,
                    fontSize = 16.sp,
                    fontFamily = interBold,
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
                        text = tourItem.location,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = tourItem.time,
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
                fontFamily = interBold,
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
                fontFamily = interBold,
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
                    fontFamily = interBold,
                    color = Color.Black
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalAmount),
                    fontSize = 16.sp,
                    fontFamily = interBold,
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
                        fontFamily = interBold,
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

fun processPayment(
    context: android.content.Context,
    eventItem: EventItem?,
    tourItem: TourItem?,
    ticketOrders: List<PaymentTicketOrder>,
    totalAmount: Int,
    navController: NavController,
    ticketViewModel: TicketViewModel,
    onComplete: () -> Unit
) {
    try {
        val orderId = "ORDER-${System.currentTimeMillis()}"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://midtrans-api-lokabudaya.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val midtransAPI = retrofit.create(MidtransAPI::class.java)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: "customer@example.com"
        val userName = currentUser?.displayName ?: "Customer"
        val itemDetails = ticketOrders.map { order ->
            ItemDetail(
                id = order.ticketTypeName.replace(" ", "_").lowercase(),
                price = order.price,
                quantity = order.quantity,
                name = order.ticketTypeName
            )
        }

        val customerDetails = CustomerDetail(
            first_name = userName.split(" ").firstOrNull() ?: "Customer",
            last_name = userName.split(" ").drop(1).joinToString(" ").ifEmpty { "Name" },
            email = userEmail,
            phone = "08123456789"
        )

        val midtransRequest = MidtransRequest(
            order_id = orderId,
            gross_amount = totalAmount,
            item_details = itemDetails,
            customer_details = customerDetails
        )

        midtransAPI.createTransaction(midtransRequest).enqueue(object : Callback<MidtransResponse> {
            override fun onResponse(call: Call<MidtransResponse>, response: Response<MidtransResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody?.success == true && !responseBody.token.isNullOrEmpty()) {
                        val snapToken = responseBody.token
                        val paymentUrl = responseBody.redirect_url

                        ticketViewModel.saveOrderBeforePayment(
                            eventItem = eventItem,
                            tourItem = tourItem,
                            ticketOrders = ticketOrders,
                            totalAmount = totalAmount,
                            snapToken = snapToken,
                            paymentUrl = paymentUrl,
                            orderId = orderId,
                            onSuccess = { savedOrderId ->
                                val fragmentActivity = context as? androidx.fragment.app.FragmentActivity
                                if (fragmentActivity != null) {
                                    MidtransSDK.getInstance().startPaymentUiFlow(
                                        fragmentActivity,
                                        snapToken
                                    )
                                } else {
                                }

                                onComplete()
                            },
                            onError = { error ->
                                onComplete()
                            }
                        )
                    } else {
                        onComplete()
                    }
                } else {
                    onComplete()
                }
            }

            override fun onFailure(call: Call<MidtransResponse>, t: Throwable) {
                onComplete()
            }
        })

    } catch (e: Exception) {
        onComplete()
    }
}