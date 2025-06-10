package com.dev.lokabudaya.pages.Profile.Menu

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.OrderData
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Ticket.TicketViewModel
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.midtrans.sdk.corekit.core.MidtransSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val userOrders by ticketViewModel.userOrders.collectAsState()
    val isLoading by ticketViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            is AuthState.Authenticated -> {
                ticketViewModel.refreshOrders()
            }
            else -> Unit
        }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            isRefreshing = true
            ticketViewModel.syncOrderStatusWithMidtrans()

            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                isRefreshing = false
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8F8F8)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .background(Color(0xFFF8F8F8))
            ) {
                PaymentSection(navController)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Orders",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = bigTextColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = selectedCategoryColor)
                    }
                } else if (userOrders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pesanan",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(userOrders) { order ->
                            OrderCard(
                                order = order,
                                onOrderClick = { orderData ->
                                    when (orderData.status) {
                                        "pending" -> {
                                            continuePayment(orderData, navController, context)
                                        }

                                        "expired" -> {
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderData,
    onOrderClick: (OrderData) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onOrderClick(order) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.eventTitle,
                        fontSize = 16.sp,
                        fontFamily = interBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = order.eventLocation,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(order.totalAmount),
                        fontSize = 16.sp,
                        fontFamily = interBold,
                        color = selectedCategoryColor
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            when (order.status) {
                                "pending" -> Color(0xFFFFA726)
                                "paid" -> Color(0xFF66BB6A)
                                "expired" -> Color(0xFFEF5350)
                                "cancelled" -> Color(0xFF9E9E9E)
                                else -> Color.Gray
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (order.status) {
                            "pending" -> "Pending"
                            "paid" -> "Paid"
                            "expired" -> "Expired"
                            "cancelled" -> "Cancelled"
                            else -> order.status
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Order ID: ${order.orderId}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = "Date: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(order.orderDate))}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            when (order.status) {
                "expired" -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Order expired",
                        fontSize = 12.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
                "pending" -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to continue payment",
                        fontSize = 12.sp,
                        color = selectedCategoryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                "paid" -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Payment completed",
                        fontSize = 12.sp,
                        color = Color.Green,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun continuePayment(
    order: OrderData,
    navController: NavController,
    context: android.content.Context
) {
    try {
        val fragmentActivity = context as? androidx.fragment.app.FragmentActivity
        if (fragmentActivity != null && order.snapToken.isNotEmpty()) {
            MidtransSDK.getInstance().startPaymentUiFlow(
                fragmentActivity,
                order.snapToken
            )
        } else {
            openPaymentUrlInBrowser(order.paymentUrl, context)
        }
    } catch (e: Exception) {
        openPaymentUrlInBrowser(order.paymentUrl, context)
    }
}

fun openPaymentUrlInBrowser(paymentUrl: String, context: android.content.Context) {
    try {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(paymentUrl)
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun PaymentSection(navController: NavController) {
    Row(
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
                        navController.navigate(ScreenRoute.Profile.route)
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Payment",
                fontSize = 24.sp,
                fontFamily = interBold,
                color = bigTextColor
            )
        }
    }
}