package com.dev.lokabudaya

sealed class ScreenRoute(val route: String) {
    object Home: ScreenRoute("HomePage")
    object Filter: ScreenRoute("FilterPage")
    object Ticket: ScreenRoute("ticketPage")
    object Book: ScreenRoute("bookPage")
    object Profile: ScreenRoute("ProfilePage")
}
