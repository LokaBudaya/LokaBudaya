package com.dev.lokabudaya.pages.Ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.poppinsLight
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DetailTicketFirestorePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    ticketId: String
) {
    val authState = authViewModel.authState.observeAsState()
    val userTickets by ticketViewModel.userTickets.collectAsState()

    // Cari ticket berdasarkan ID
    val ticketData = userTickets.find { it.id == ticketId }

    // Get current user display name
    var displayName by remember { mutableStateOf("User") }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"
    }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }

    if (ticketData == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading ticket details...")
            }
        }
        return
    }

    // PERBAIKAN: Gunakan layout yang sama dengan DetailTicketPage
    var heightPx by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                heightPx = coordinates.size.height.toFloat()
            }
            .background(
                brush = Brush.verticalGradient(
                    0.4f to Color(0xFF91ADFB),
                    1.0f to Color(0xFF01103A),
                    startY = 0.0f,
                    endY = heightPx
                )
            )
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Back button - sama dengan DetailTicketPage
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(556.dp)
                    .background(Color.Black.copy(alpha = .35f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp, bottom = 44.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (ticketData.eventImageRes != 0) ticketData.eventImageRes
                            else R.drawable.img_event
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .height(164.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event title
                    Text(
                        text = ticketData.eventTitle,
                        fontSize = 28.sp,
                        fontFamily = poppinsSemiBold,
                        color = White,
                        lineHeight = 28.sp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event description - buat deskripsi dari ticket orders
                    val ticketDescription = buildString {
                        append("Tiket untuk event ${ticketData.eventTitle}. ")
                        ticketData.ticketOrders.forEach { order ->
                            append("${order.quantity}x ${order.ticketTypeName}. ")
                        }
                        append("Total: ${ticketData.totalQuantity} tiket.")
                    }

                    Text(
                        text = ticketDescription,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontFamily = poppinsLight,
                        color = White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Details section - sama dengan DetailTicketPage
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Nama
                            Column {
                                Text(
                                    text = "Nama",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    text = displayName,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Time
                            Column {
                                Text(
                                    text = "Time",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    text = ticketData.eventTime,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                        }

                        Column {
                            // Date
                            Column {
                                Text(
                                    text = "Date",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    text = formatTicketDate(ticketData.eventStartDate, ""),
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Seat/Quantity
                            Column {
                                Text(
                                    text = "Quantity",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    text = ticketData.totalQuantity.toString(),
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Barcode section
                    Text(
                        text = "Scan This Barcode",
                        fontSize = 12.sp,
                        fontFamily = poppinsLight,
                        color = White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Barcode
                    Text(
                        text = ticketData.id.take(12),
                        fontFamily = FontFamily(Font(R.font.libre_barcode_128)),
                        color = White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 84.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Download button - sama dengan DetailTicketPage
        Button(
            onClick = {
                // TODO: Implement download functionality
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF91ADFB),
                contentColor = Color(0xFF0C1F54)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Download Ticket",
                fontSize = 16.sp,
                fontFamily = interBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}