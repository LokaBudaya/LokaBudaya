package com.dev.lokabudaya

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Auth.LoginPage
import com.dev.lokabudaya.pages.Auth.SignupPage
import com.dev.lokabudaya.pages.Book.BookPage
import com.dev.lokabudaya.pages.Home.Blog.BlogPage
import com.dev.lokabudaya.pages.Home.HomePage
import com.dev.lokabudaya.pages.Profile.ProfilePage
import com.dev.lokabudaya.pages.Search.Kuliner.CulinaryPage
import com.dev.lokabudaya.pages.Home.Category.EventPage
import com.dev.lokabudaya.pages.Search.SearchPage
import com.dev.lokabudaya.pages.Home.Category.TourPage
import com.dev.lokabudaya.pages.Profile.Menu.AccessibilityPage
import com.dev.lokabudaya.pages.Profile.Menu.ActivityPage
import com.dev.lokabudaya.pages.Profile.Menu.EditProfilePage
import com.dev.lokabudaya.pages.Profile.Menu.NotificationPage
import com.dev.lokabudaya.pages.Profile.Menu.PaymentPage
import com.dev.lokabudaya.pages.Profile.Menu.PrivacyPage
import com.dev.lokabudaya.pages.Ticket.TicketPage
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.fabColor
import com.dev.lokabudaya.ui.theme.navColor

@Composable
fun MainScreen(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {

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
        bottomBar = {
            if (currentRoute == ScreenRoute.Home.route ||
                currentRoute == ScreenRoute.Ticket.route ||
                currentRoute == ScreenRoute.Book.route ||
                currentRoute == ScreenRoute.Profile.route ||
                currentRoute == ScreenRoute.Search.route
                ) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->

        val graph =
            navController.createGraph(startDestination = ScreenRoute.Home.route) {
                composable(route = ScreenRoute.Login.route) {
                    LoginPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Signup.route) {
                    SignupPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Home.route) {
                    HomePage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Search.route) {
                    SearchPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Ticket.route) {
                    TicketPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Book.route) {
                    BookPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Profile.route) {
                    ProfilePage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Culinary.route) {
                    CulinaryPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Event.route) {
                    EventPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Tour.route) {
                    TourPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Blog.route) {
                    BlogPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Privacy.route) {
                    PrivacyPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Activity.route) {
                    ActivityPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Notification.route) {
                    NotificationPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.EditProfile.route) {
                    EditProfilePage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Accessibility.route) {
                    AccessibilityPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Payment.route) {
                    PaymentPage(modifier, navController, authViewModel)
                }
            }
        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(tween(150))
            },
            exitTransition = {
                fadeOut(tween(150))
            }
        )

    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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