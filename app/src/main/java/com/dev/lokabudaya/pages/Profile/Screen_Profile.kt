package com.dev.lokabudaya.pages.Profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {    // ambil argumen userID
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }
    val interactionSource = remember {MutableInteractionSource()}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        ProfileTopBar(interactionSource)

        Spacer(modifier = Modifier.height(16.dp))

        ProfileTag(interactionSource, navController)

        Spacer(modifier = Modifier.height(20.dp))

        ProfileMyBlogMyTrip(24, 2, interactionSource)

        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem("Activity", "Pantau aktivitas harian", interactionSource, authViewModel, navController)
        ProfileMenuItem("Payment", "Cek pembayaran tiket", interactionSource, authViewModel, navController)
        ProfileMenuItem("Accessibility", "Kemudahan penggunaan aplikasi", interactionSource, authViewModel, navController)
        ProfileMenuItem("Privacy", "Privasi aplikasi dan data", interactionSource, authViewModel, navController)
        ProfileMenuItem("Notifications", "Atur notifikasi aplikasi", interactionSource, authViewModel, navController)
        ProfileMenuItem("Log out", "Keluar dari sesi saat ini", interactionSource, authViewModel, navController)
    }
}

@Composable
fun ProfileTopBar(interactionSource: MutableInteractionSource) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            ),
            modifier = Modifier.weight(1f)
        )
//        Icon(
//            painter = painterResource(id = R.drawable.ic_setting),
//            contentDescription = "Settings",
//            tint = bigTextColor,
//            modifier = Modifier
//                .clickable(
//                    interactionSource = interactionSource,
//                    indication = null,
//                    onClick = {}
//                )
//        )
    }
}

@Composable
fun ProfileTag(interactionSource: MutableInteractionSource, navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_banner),
            //if (userID.profilePict != null){
            // painterResource(userID.profilePict)
            // else painterResource(id = R.drawble.ic_img_banner
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
//                  text = userID.name
                text = "Admin",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1A2758)
            )
            Text(
//                  text = userID.email
                text = "@admin",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = "Edit",
            tint = bigTextColor,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        navController.navigate("EditProfilePage")
                    }
                )
        )
    }
}

@Composable
fun ProfileMenuItem(title: String, subtitle: String, interactionSource: MutableInteractionSource, authViewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val imageRes = when (title) {
        "Activity" -> R.drawable.ic_activity
        "Payment" -> R.drawable.ic_payment
        "Accessibility" -> R.drawable.ic_accessibility
        "Privacy" -> R.drawable.ic_privacy
        "Notifications" -> R.drawable.ic_notif
        "Log out" -> R.drawable.ic_logout
        else -> R.drawable.ic_activity
    }
    Column (
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (title == "Log out") {
                        authViewModel.signout(context)
                    }
                    if (title == "Privacy") {
                        navController.navigate("PrivacyPage")
                    }
                    if (title == "Activity") {
                        navController.navigate("ActivityPage")
                    }
                    if (title == "Notifications") {
                        navController.navigate("NotificationPage")
                    }
                    if (title == "Payment") {
                        navController.navigate("PaymentPage")
                    }
                    if (title == "Accessibility") {
                        navController.navigate("AccessibilityPage")
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                tint = selectedCategoryColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_forward),
                contentDescription = "Arrow forward",
                tint = Color(0xFF1A2758),
                modifier = Modifier.size(12.dp)
            )
        }
        HorizontalDivider(thickness = 2.dp, color = Color(0xFFE0E0E0))
    }
}

@Composable
fun ProfileMyBlogMyTrip(blogCount: Int, tripCount: Int, interactionSource: MutableInteractionSource) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Blog
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(
                    bottomStart = 16.dp,
                    topStart = 16.dp
                ))
                .width(144.dp)
                .wrapContentWidth()
                .fillMaxHeight()
                .background(Color(0xFFEDC3EF)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(R.drawable.img_profile_myblog_background),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Blog",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable (
                                onClick = {},
                                interactionSource = interactionSource,
                                indication = null
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_myblog_button),
                            contentDescription = null
                        )
                    }
                }
                Text(
                    text = blogCount.toString(),
                    fontSize = 56.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        
        // My Trip
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(
                    bottomEnd = 16.dp,
                    topEnd = 16.dp
                ))
                .width(144.dp)
                .wrapContentWidth()
                .fillMaxHeight()
                .background(Color(0xFFC5C3EF)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(R.drawable.img_profile_mytrip_background),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Trip",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable (
                                onClick = {},
                                interactionSource = interactionSource,
                                indication = null
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_mytrip_button),
                            contentDescription = null
                        )
                    }
                }
                Text(
                    text = tripCount.toString(),
                    fontSize = 56.sp,
                    color = Color.White
                )
            }
        }
    }
}