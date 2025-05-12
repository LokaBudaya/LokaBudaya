package com.dev.lokabudaya.data

import com.dev.lokabudaya.R
import com.dev.lokabudaya.pages.Home.EventItem
import com.dev.lokabudaya.pages.Book.WishlistItem
import com.dev.lokabudaya.pages.Home.BlogCardClass
import com.dev.lokabudaya.pages.Home.Place

object DataProvider {
    val recommendedPlaces = listOf(
        Place(
            title = "Mangkunegaran",
            location = "Surakarta",
            imageRes = R.drawable.img_mangkunegaran
        ),
        Place(
            title = "Candi Borobudur",
            location = "Magelang",
            imageRes = R.drawable.img_borobudur
        ),
        Place(
            title = "Pasar Gede",
            location = "Surakarta",
            imageRes = R.drawable.img_pasargede
        )
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
    val wishlistItems = List(7) {
        WishlistItem(
            title = "Mangkunegaran",
            subtitle = "istana resmi Kadipaten Mangkunegaran",
            location = "Surakarta",
            price = "Rp 30.000 - Rp 50.000"
        )
    }

    val blogCards = listOf(
        BlogCardClass(
            title = "My Blog #1",
            desc = "ini blog pertamaku ges, salam kenal semuanya!!",
            imageId = R.drawable.img_event
        ),
        BlogCardClass(
            title = "My Blog #2",
            desc = "ini blog keduaku ges, salam kenal semuanya!!",
            imageId = R.drawable.img_event
        ),
        BlogCardClass(
            title = "My Blog #3",
            desc = "ini blog ketigaku ges, salam kenal semuanya!!",
            imageId = R.drawable.img_event
        ),
        BlogCardClass(
            title = "My Blog #4",
            desc = "ini blog keempatku ges, salam kenal semuanya!!",
            imageId = R.drawable.img_event
        ),
        BlogCardClass(
            title = "My Blog #5",
            desc = "ini blog kelimaku ges, salam kenal semuanya!!",
            imageId = R.drawable.img_event
        ),
        BlogCardClass(
            title = "My Blog #6",
            desc = "ini blog keenamku ges, salam kenal semuanya!!",
            imageId = R.drawable.img_event
        )

    )
}