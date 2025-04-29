package com.dev.lokabudaya.pages

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor

@Composable
fun ProfilePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
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
            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "Settings",
                tint = bigTextColor
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_banner),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Admin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A2758)
                )
                Text(
                    text = "@admin",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Edit",
                tint = bigTextColor
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF3E6FF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "My Blog",
                            color = Color(0xFFB388FF),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_location),
//                            contentDescription = "Blog Icon",
//                            tint = Color(0xFFB388FF),
//                            modifier = Modifier.size(18.dp)
//                        )
                    }
                    Text(
                        text = "0",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Color(0xFFB388FF)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFE3E6FF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "My Trip",
                            color = Color(0xFFB3B8FF),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_location),
//                            contentDescription = "Trip Icon",
//                            tint = Color(0xFFB3B8FF),
//                            modifier = Modifier.size(18.dp)
//                        )
                    }
                    Text(
                        text = "0",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Color(0xFFB3B8FF)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ProfileMenuItem("Activity", "Pantau aktivitas harian")
        ProfileMenuItem("Payment", "Cek pembayaran tiket")
        ProfileMenuItem("Accessibility", "Kemudahan penggunaan aplikasi")
        ProfileMenuItem("Privacy", "Privasi aplikasi dan data")
        ProfileMenuItem("Notifications", "Atur notifikasi aplikasi")
        ProfileMenuItem("Log out", "Keluar dari sesi saat ini")
    }
}

@Composable
fun ProfileMenuItem(title: String, subtitle: String) {
    val imageRes = when (title) {
        "Activity" -> R.drawable.ic_activity
        "Payment" -> R.drawable.ic_payment
        "Accessibility" -> R.drawable.ic_accessibility
        "Privacy" -> R.drawable.ic_privacy
        "Notifications" -> R.drawable.ic_notif
        "Log out" -> R.drawable.ic_logout
        else -> R.drawable.ic_activity
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .padding(start = 12.dp)
                .clickable { },
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