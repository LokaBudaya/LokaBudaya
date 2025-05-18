package com.dev.lokabudaya

sealed class ScreenRoute(val route: String) {
    object Home: ScreenRoute("HomePage")
    object Search: ScreenRoute("SearchPage")
    object Ticket: ScreenRoute("ticketPage")
    object Book: ScreenRoute("bookPage")
    object Profile: ScreenRoute("ProfilePage")
    object Login: ScreenRoute("LoginPage")
    object Signup: ScreenRoute("SignupPage")
    object Culinary: ScreenRoute("CulinaryPage")
    object Event: ScreenRoute("EventPage")
    object Tour: ScreenRoute("TourPage")
}