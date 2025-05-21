package com.dev.lokabudaya.pages.Profile.Menu

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Profile.Menu.Notification.NotificationDataStore
import com.dev.lokabudaya.pages.Profile.Menu.Notification.NotificationToggleSection
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.smallTextColor
import kotlinx.coroutines.launch

@Composable
fun PrivacyPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Column(modifier = modifier.padding(16.dp)) {
        HeaderPrivacySection(navController)
        Spacer(modifier = Modifier.height(32.dp))
        PrivacySection()
    }
}

// Header Privacy Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderPrivacySection(navController: NavController) {
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
                text = "Privacy & Security",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
    }
}

@Composable
fun PrivacySection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_privacy),
                contentDescription = "Privacy Icon",
                tint = smallTextColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Change Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = bigTextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Change your password to secure your account if needed",
                    fontSize = 12.sp,
                    color = bigTextColor,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_forward),
            contentDescription = "Forward Icon",
            tint = bigTextColor,
            modifier = Modifier.size(24.dp)
        )
    }
}