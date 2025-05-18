package com.dev.lokabudaya.data

import com.dev.lokabudaya.R

data class WishlistItem(
    val title: String,
    val subtitle: String,
    val location: String,
    val price: String
)

data class CulinaryItem(
    val title: String,
    val subtitle: String,
    val rating: Double,
    val location: String,
    val imageRes: Int,
    val price: String
)

data class TourItem(
    val title: String,
    val subtitle: String,
    val rating: Double,
    val location: String,
    val imageRes: Int,
    val price: String
)

data class EventItem(
    val title: String,
    val imageRes: Int,
    val rating: Double,
    val category: String,
    val location: String,
    val time: String,
    val price: String
)

data class BlogCardClass(
    val title: String,
    val desc: String,
    val imageId: Int
)

object DataProvider {
    val culinaryList = listOf(
        CulinaryItem(
            title = "Bestik Pak Darmo",
            subtitle = "Daging bestik empuk lezat",
            rating = 4.9,
            location = "Surakarta",
            imageRes = R.drawable.img_bestik,
            price = "15.000 - 50.000"
        ),
        CulinaryItem(
            title = "Teh Tarik 88",
            subtitle = "Ngeteh dulu cuy",
            rating = 4.7,
            location = "Surakarta",
            imageRes = R.drawable.img_tehtarik,
            price = "5.000 - 30.000"
        ),
        CulinaryItem(
            title = "Es Krim Tentrem",
            subtitle = "Es krim jadoel khas Surakarta",
            rating = 4.8,
            location = "Surakarta",
            imageRes = R.drawable.img_estentrem,
            price = "20.000 - 70.000"
        )
    )
    val tourList = listOf(
        TourItem(
            title = "Mangkunegaran",
            subtitle = "istana resmi Kadipaten Mangkunegaran",
            rating = 4.8,
            location = "Surakarta",
            imageRes = R.drawable.img_mangkunegaran,
            price = "35.000"
        ),
        TourItem(
            title = "Candi Borobudur",
            subtitle = "candi Buddha terbesar di dunia",
            rating = 4.8,
            location = "Magelang",
            imageRes = R.drawable.img_borobudur,
            price = "35.000"
        ),
        TourItem(
            title = "Pasar Gede",
            subtitle = "Pasar Gede Hardjonagoro terbesar di Solo",
            rating = 4.8,
            location = "Surakarta",
            imageRes = R.drawable.img_pasargede,
            price = "35.000"
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
            price = "30.000"
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