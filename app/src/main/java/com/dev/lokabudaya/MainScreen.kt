package com.dev.lokabudaya

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dev.lokabudaya.pages.HomePage

@Composable
fun MainScreen() {
    var selectedIcon by remember { mutableStateOf(-1) }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val icons = listOf(
                            R.drawable.ic_home to "Home",
                            R.drawable.ic_filter to "Filter",
                            R.drawable.ic_ticket to "Ticket",
                            R.drawable.ic_book to "Book",
                            R.drawable.ic_profile to "Profile"
                        )

                        icons.forEachIndexed { index, (iconRes, contentDescription) ->
                            IconButton(onClick = { selectedIcon = index }) {
                                Icon(
                                    painter = painterResource(iconRes),
                                    contentDescription = contentDescription,
                                    modifier = Modifier.size(28.dp),
                                    tint = if (selectedIcon == index) Color.Black else Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        HomePage(modifier = Modifier.padding(innerPadding))
    }
}