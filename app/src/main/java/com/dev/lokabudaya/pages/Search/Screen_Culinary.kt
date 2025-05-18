package com.dev.lokabudaya.pages.Search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.bigTextColor

//Culinary Page
@Composable
fun CulinaryPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Box(modifier = modifier.padding(12.dp)) {
        HeaderCulinarySection()
    }
}

// Header Culinary Section
@Composable
fun HeaderCulinarySection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Kuliner",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
            )
        }
        SearchIcon()
    }
}