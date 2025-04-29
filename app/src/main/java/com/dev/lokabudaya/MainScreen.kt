package com.dev.lokabudaya

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.dev.lokabudaya.pages.BookPage
import com.dev.lokabudaya.pages.HomePage
import com.dev.lokabudaya.pages.ProfilePage
import com.dev.lokabudaya.pages.SearchPage
import com.dev.lokabudaya.pages.TicketPage
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.fabColor
import com.dev.lokabudaya.ui.theme.navColor

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            if (currentRoute == ScreenRoute.Home.route) {
                FloatingActionButton(
                    containerColor = fabColor,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 24.dp),
                    onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_crosshair),
                        contentDescription = null, tint = White
                    )
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        val graph =
            navController.createGraph(startDestination = ScreenRoute.Home.route) {
                composable(route = ScreenRoute.Home.route) {
                    HomePage(modifier = Modifier)
                }
                composable(route = ScreenRoute.Search.route) {
                    SearchPage()
                }
                composable(route = ScreenRoute.Ticket.route) {
                    TicketPage()
                }
                composable(route = ScreenRoute.Book.route) {
                    BookPage()
                }
                composable(route = ScreenRoute.Profile.route) {
                    ProfilePage()
                }
            }
        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )

    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    val navigationItems = listOf(
        NavigationItem(
            icon = R.drawable.ic_home,
            route = ScreenRoute.Home.route
        ),
        NavigationItem(
            icon = R.drawable.ic_search,
            route = ScreenRoute.Search.route
        ),
        NavigationItem(
            icon = R.drawable.ic_ticket,
            route = ScreenRoute.Ticket.route
        ),
        NavigationItem(
            icon = R.drawable.ic_book,
            route = ScreenRoute.Book.route
        ),
        NavigationItem(
            icon = R.drawable.ic_profile,
            route = ScreenRoute.Profile.route
        )
    )

    NavigationBar(
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "Nav icon"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = navColor,
                    selectedIconColor = Color.Black,
                    indicatorColor = Color.Transparent
                )

            )
        }
    }
}

data class NavigationItem(
    val icon: Int,
    val route: String
)