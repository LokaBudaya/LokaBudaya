package com.dev.lokabudaya.data

import com.dev.lokabudaya.R
import com.dev.lokabudaya.pages.Home.EventItem

object DataProvider {
    val recommendedItems = listOf(
        "Mangkunegaran",
        "Candi Borobudur",
        "Pasar Gede"
    )
    val eventList = listOf(
        EventItem(
            title = "Wayang Kulit",
            imageRes = R.drawable.img_reogponorogo,
            rating = 4.5,
            category = "Pertunjukan Seni",
            location = "Surakarta",
            time = "16:00 WIB",
            price = "90.000"
        ),
        EventItem(
            title = "Tari Saman",
            imageRes = R.drawable.img_event,
            rating = 4.8,
            category = "Pertunjukan Seni",
            location = "Yogyakarta",
            time = "13:00 WIB",
            price = "20.000"
        ),
        EventItem(
            title = "Tari Tor-tor",
            imageRes = R.drawable.img_event,
            rating = 4.4,
            category = "Pertunjukan Seni",
            location = "Purwokerto",
            time = "09:00 WIB",
            price = "35.000"
        )
    )
}