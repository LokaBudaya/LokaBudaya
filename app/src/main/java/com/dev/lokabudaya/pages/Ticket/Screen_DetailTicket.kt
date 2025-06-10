package com.dev.lokabudaya.pages.Ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.dev.lokabudaya.data.TicketItem
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.poppinsLight
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.interBold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Detail ticket Screen
@Composable
fun DetailTicketPage(
    modifier: Modifier = Modifier, 
    navController: NavController, 
    authViewModel: AuthViewModel,
    ticketItem: TicketItem
) {
    val authState = authViewModel.authState.observeAsState()
    var currentUser by remember { mutableStateOf<FirebaseUser?>(null) }
    var displayName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        currentUser = FirebaseAuth.getInstance().currentUser
        displayName = currentUser?.displayName ?: "User"
    }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> {
                currentUser = FirebaseAuth.getInstance().currentUser
                displayName = currentUser?.displayName ?: "User"
            }
        }
    }

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
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp, bottom = 44.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(ticketItem.image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .height(164.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text =ticketItem.title,
                        fontSize = 28.sp,
                        fontFamily = poppinsSemiBold,
                        color = White,
                        lineHeight = 28.sp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = ticketItem.detailedDesc,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Justify,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontFamily = poppinsLight,
                        color = White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
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
                            Column {
                                Text(
                                    text = "Time",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    text = ticketItem.date,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                        }
                        Column {
                            Column {
                                Text(
                                    text = "Date",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    // text = ticket.time
                                    text = ticketItem.date,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Column {
                                Text(
                                    text = "Seat",
                                    fontSize = 14.sp,
                                    fontFamily = poppinsLight,
                                    color = White
                                )
                                Text(
                                    // text = ticket.seat
                                    text = "4",
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold,
                                    lineHeight = 16.sp,
                                    color = White
                                )
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = "Scan This Barcode",
                        fontSize = 12.sp,
                        fontFamily = poppinsLight,
                        color = White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    // Barcode
                    Text(
                        text = ticketItem.id,
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
        Button(
            onClick = {},
            colors = ButtonColors(
                containerColor = Color(0xFF91ADFB),
                contentColor = Color(0xFF0C1F54),
                disabledContentColor = Color(0xFF0C1F54),
                disabledContainerColor = Color(0xFF91ADFB)
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