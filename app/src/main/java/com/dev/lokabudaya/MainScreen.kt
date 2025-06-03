package com.dev.lokabudaya

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.DataProvider.eventItemLists
import com.dev.lokabudaya.data.DataProvider.kulinerItemLists
import com.dev.lokabudaya.data.DataProvider.ticketItemLists
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Auth.EmailVerificationPage
import com.dev.lokabudaya.pages.Auth.LoginPage
import com.dev.lokabudaya.pages.Auth.SignupPage
import com.dev.lokabudaya.pages.Book.BookPage
import com.dev.lokabudaya.pages.Culinary.DetailCulinaryPage
import com.dev.lokabudaya.pages.Home.Blog.BlogPage
import com.dev.lokabudaya.pages.Home.HomePage
import com.dev.lokabudaya.pages.Profile.ProfilePage
import com.dev.lokabudaya.pages.Home.Category.Culinary.CulinaryPage
import com.dev.lokabudaya.pages.Home.Category.Event.DetailEventPage
import com.dev.lokabudaya.pages.Home.Category.Event.EventPage
import com.dev.lokabudaya.pages.Home.Category.Tour.DetailTourPage
import com.dev.lokabudaya.pages.Search.SearchPage
import com.dev.lokabudaya.pages.Home.Category.Tour.TourPage
import com.dev.lokabudaya.pages.Map.MapPage
import com.dev.lokabudaya.pages.Profile.Menu.AccessibilityPage
import com.dev.lokabudaya.pages.Profile.Menu.ActivityPage
import com.dev.lokabudaya.pages.Profile.Menu.EditProfilePage
import com.dev.lokabudaya.pages.Profile.Menu.Notification.NotificationPage
import com.dev.lokabudaya.pages.Profile.Menu.PaymentPage
import com.dev.lokabudaya.pages.Profile.Menu.PrivacyPage
import com.dev.lokabudaya.pages.Ticket.DetailTicketPage
import com.dev.lokabudaya.pages.Ticket.TicketDetailPage
import com.dev.lokabudaya.pages.Ticket.TicketPage
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.fabColor
import com.dev.lokabudaya.ui.theme.navColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    val startDestination = remember(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user?.isEmailVerified == true) {
                    ScreenRoute.Home.route
                } else {
                    "EmailVerificationPage/${user?.email ?: ""}"
                }
            }
            is AuthState.EmailNotVerified -> {
                val user = FirebaseAuth.getInstance().currentUser
                "EmailVerificationPage/${user?.email ?: ""}"
            }
            is AuthState.Unauthenticated -> ScreenRoute.Login.route
            else -> ScreenRoute.Login.route
        }
    }

    if (authState.value == null || authState.value is AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = selectedCategoryColor)
        }
        return
    }

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
                    onClick = {
                        try {
                            navController.navigate("MapPage")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to open map", Toast.LENGTH_SHORT).show()
                        }
                    }) {
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
            navController.createGraph(startDestination = startDestination) {
                composable(route = ScreenRoute.Login.route) {
                    LoginPage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Signup.route) {
                    SignupPage(modifier, navController, authViewModel)
                }
                composable(
                    route = "EmailVerificationPage/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    EmailVerificationPage(
                        modifier = modifier,
                        navController = navController,
                        authViewModel = authViewModel,
                        userEmail = email
                    )
                }
                composable(route = ScreenRoute.Home.route) {
                    HomePage(modifier, navController, authViewModel)
                }
                composable(route = ScreenRoute.Search.route) {
                    SearchPage(modifier, navController, authViewModel)
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
                composable(route = ScreenRoute.Ticket.route) {
                    TicketPage(modifier, navController, authViewModel)
                }
                composable(
                    route = "TicketDetailPage/{eventIndex}",
                    arguments = listOf(navArgument("eventIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val eventIndex = backStackEntry.arguments?.getInt("eventIndex") ?: 0
                    val eventItem = DataProvider.eventItemLists[eventIndex]
                    TicketDetailPage(
                        modifier = modifier,
                        navController = navController,
                        eventItem = eventItem
                    )
                }
                composable(
                    route = "DetailTicketPage/{ticketIndex}",
                    arguments = listOf(navArgument("ticketIndex") {type = NavType.IntType})
                ) { backEntry ->
                    val ticketIndex = backEntry.arguments?.getInt("ticketIndex") ?: 0
                    val ticketItem = ticketItemLists[ticketIndex]
                    DetailTicketPage(
                        modifier,
                        navController = navController,
                        authViewModel = authViewModel,
                        ticketItem = ticketItem
                    )
                }
                composable(
                    route = "DetailCulinaryPage/{kulinerIndex}",
                    arguments = listOf(navArgument("kulinerIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val kulinerIndex = backStackEntry.arguments?.getInt("kulinerIndex") ?: 0
                    val kulinerItem = kulinerItemLists[kulinerIndex]
                    DetailCulinaryPage(
                        modifier = modifier,
                        navController = navController,
                        authViewModel = authViewModel,
                        kulinerItem = kulinerItem
                    )
                }
                composable(
                    route = "DetailEventPage/{eventIndex}",
                    arguments = listOf(navArgument("eventIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val eventIndex = backStackEntry.arguments?.getInt("eventIndex") ?: 0
                    val eventItem = eventItemLists[eventIndex]
                    DetailEventPage(
                        modifier = modifier,
                        navController = navController,
                        authViewModel = authViewModel,
                        eventItem = eventItem
                    )
                }
                composable(
                    route = "DetailTourPage/{tourIndex}",
                    arguments = listOf(navArgument("tourIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val tourIndex = backStackEntry.arguments?.getInt("tourIndex") ?: 0
                    val tourItem = DataProvider.tourItemLists[tourIndex]
                    DetailTourPage(
                        modifier = modifier,
                        navController = navController,
                        authViewModel = authViewModel,
                        tourItem = tourItem
                    )
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
                composable(route = ScreenRoute.Map.route) {
                    MapPage(modifier, navController, authViewModel)
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