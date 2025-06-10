package com.dev.lokabudaya.pages.Profile.Menu.Notification

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.smallTextColor
import kotlinx.coroutines.launch

@Composable
fun NotificationPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8)
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .background(Color(0xFFF8F8F8))
        ) {
            HeaderNotificationSection(navController)
            Spacer(modifier = Modifier.height(32.dp))
            NotificationToggleSection()
        }
    }
}

// Header Activity Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderNotificationSection(navController: NavController) {
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
                text = "Notification",
                fontSize = 24.sp,
                fontFamily = interBold,
                color = bigTextColor
            )
        }
    }
}

@Composable
fun NotificationToggleSection() {
    val context = LocalContext.current
    val dataStore = remember { NotificationDataStore(context) }
    val scope = rememberCoroutineScope()

    val notificationEnabledState = dataStore.notificationEnabled.collectAsState(initial = null)

    val notificationEnabled = notificationEnabledState.value

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = "Notification Icon",
                tint = Color(0xFF2D4B9A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "App notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = bigTextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Click the toggle button to turn the notifications off or turn the notifications on",
                    fontSize = 12.sp,
                    color = bigTextColor,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }

        if (notificationEnabled != null) {
            Switch(
                checked = notificationEnabled,
                onCheckedChange = { isEnabled ->
                    scope.launch {
                        dataStore.saveNotificationEnabled(isEnabled)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = smallTextColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF2D4B9A),
                strokeWidth = 2.dp
            )
        }
    }
}