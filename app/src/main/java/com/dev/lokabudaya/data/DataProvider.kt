package com.dev.lokabudaya.data

import androidx.compose.ui.graphics.Color
import com.dev.lokabudaya.R
import java.util.UUID

data class BlogCardClass(
    // kurang user
    val title: String,
    val desc: String,
    val imageId: Int
)

data class KulinerItem(
    val id: String = UUID.randomUUID().toString(),
    val imgRes:Int,
    val title:String,
    val price:Int,
    val rating:Double,
    val location:String,
    var isFavorite:Boolean,
    var desc:String,
    var latitude:Double,
    var longtitude:Double,
    val label: String = "Kuliner",
    val backgroundLabelColor: Color = Color(0xFFFFEAC3),
    val textLabelColor: Color = Color(0xFFEA8D00)
)
data class EventItem(
    val id: String = UUID.randomUUID().toString(),
    val imgRes:Int,
    val title:String,
    val price:Int,
    val rating:Double,
    val location:String,
    var isFavorite:Boolean,
    val label:String = "Event",
    val category: String,
    val desc:String,
    val latitude:Double,
    val longtitude:Double,
    val eventDate:String,
    val eventTime:String,
    val backgroundLabelColor: Color = Color(0xFFFFC3E8),
    val textLabelColor: Color = Color(0xFFEA00DB)
)
data class TourItem(
    val id: String = UUID.randomUUID().toString(),
    val imgRes:Int,
    val title:String,
    val price:Int,
    val rating:Double,
    val location:String,
    var isFavorite:Boolean,
    var desc:String,
    var latitude:Double,
    var longtitude:Double,
    val label:String = "Tour",
    val backgroundLabelColor: Color = Color(0xFFC3F2FF),
    val textLabelColor: Color = Color(0xFF00B6EA)
)
data class Ticket(
    val title:String,
    val date:String,
    val location:String,
    val qrCode:Int
)

object DataProvider {
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
    val kulinerItemLists = listOf(
        KulinerItem(
            imgRes = R.drawable.img_bestik,
            title = "Bestik Pak Darmo",
            price = 15000,
            rating = 4.9,
            location = "Surakarta",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Bestik Pak Darmo",
            price = 15000,
            rating = 4.9,
            location = "Surakarta",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.5709241,
            longtitude = 110.7926132
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.5709241,
            longtitude = 110.7926132
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.5709241,
            longtitude = 110.7926132
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.5709241,
            longtitude = 110.7926132
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.5709241,
            longtitude = 110.7926132
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false,
            desc = "INI DESKRIPSI MAKANAN",
            latitude = -7.5709241,
            longtitude = 110.7926132
        ),

    )
    val eventItemLists = listOf(
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        ),
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        ),
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        ),
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        ),
        EventItem(
            imgRes = R.drawable.img_reogponorogo,
            title = "Wayang Kulit",
            price = 90000,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        ),
        EventItem(
            title = "Tari Saman",
            imgRes = R.drawable.img_event,
            rating = 4.8,
            location = "Yogyakarta",
            price = 20000,
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        ),
        EventItem(
            title = "Tari Tor-tor",
            imgRes = R.drawable.img_event,
            rating = 4.4,
            location = "Purwokerto",
            price = 35000,
            isFavorite = false,
            category = "Pertunjukan Seni",
            desc = "INI DESKRIPSI EVENT",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339,
            eventDate = "2025-12-31",
            eventTime = "19:00"
        )
    )
    val tourItemLists = listOf(
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            desc = "INI DESKIRPSI TOUR",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339
        ),
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Yogyakarta",
            isFavorite = false,
            desc = "INI DESKIRPSI TOUR",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339
        ),
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Yogyakarta",
            isFavorite = false,
            desc = "INI DESKIRPSI TOUR",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339
        ),
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false,
            desc = "INI DESKIRPSI TOUR",
            latitude = -7.574178450295152,
            longtitude = 110.81591618151339
        )
    )
    val myTickets = listOf(
        Ticket(
            title = "Artjog 2025",
            date = "25 Agustus 2525",
            location = "Universitas Sebelas Maret, Yogyakarta",
            qrCode = R.drawable.img_qrcode_dummy
        )
    )
}