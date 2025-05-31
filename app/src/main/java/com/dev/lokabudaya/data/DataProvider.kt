package com.dev.lokabudaya.data

import androidx.compose.ui.graphics.Color
import com.dev.lokabudaya.R
import java.util.UUID

data class BlogCardClass(
    // kurang user
    val title: String,
    val content: String,
    val imageId: Int,
    val date: String,
    val viewers: String
)

data class KulinerItem(
    val id: String = UUID.randomUUID().toString(),
    val imgRes:Int,
    val title:String,
    val price:Int,
    val rating:Double,
    val location:String,
    var isFavorite:Boolean,
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
    val time: String, //harusnya pake time
    var isFavorite:Boolean,
    val label: String = "Event",
    val category: String,
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
            title = "Ya Allah Mudahkanlah Segala Urusanku",
            content = "wow tempat ini sangat seru kalian harus datang kesini sama siapapun yang kalian mau cocok untuk semua orang",
            imageId = R.drawable.img_event,
            date = "25 September 2025",
            viewers = "1.4K"
        ),
        BlogCardClass(
            title = "My Blog #2",
            content = "ini blog keduaku ges, salam kenal semuanya!! woowowoo bla bla bla ble bleb le",
            imageId = R.drawable.img_event,
            date = "25 September 2025",
            viewers = "1.4K"
        ),
        BlogCardClass(
            title = "My Blog #3",
            content = "ini blog ketigaku ges, salam kenal semuanya!! oke gas oke gas oke gas",
            imageId = R.drawable.img_event,
            date = "25 September 2025",
            viewers = "1.4K"
        ),
        BlogCardClass(
            title = "My Blog #4",
            content = "ini blog keempatku ges, salam kenal semuanya!! cape mau tidur cape mau tidur",
            imageId = R.drawable.img_event,
            date = "25 September 2025",
            viewers = "1.4K"
        ),
        BlogCardClass(
            title = "My Blog #5",
            content = "ini blog kelimaku ges, salam kenal semuanya!! malas pingin hiling malas pingin hiling malas pingin hiling malas pingin hiling",
            imageId = R.drawable.img_event,
            date = "25 September 2025",
            viewers = "1.4K"
        ),
        BlogCardClass(
            title = "My Blog #6",
            content = "ini blog keenamku ges, salam kenal semuanya!! mau belajar uas mau belajar mau belajar mau belajar mau belajar uas mau bleajar uas pls",
            imageId = R.drawable.img_event,
            date = "25 September 2025",
            viewers = "1.4K"
        )
    )
    val kulinerItemLists = listOf(
        KulinerItem(
            imgRes = R.drawable.img_bestik,
            title = "Bestik Pak Darmo",
            price = 15000,
            rating = 4.9,
            location = "Surakarta",
            isFavorite = false
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Bestik Pak Darmo",
            price = 15000,
            rating = 4.9,
            location = "Surakarta",
            isFavorite = false
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false
        ),
        KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Gacoan Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakata",
            isFavorite = false
        ),

    )
    val eventItemLists = listOf(
        // kurang user
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            time = "14.00",
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni"
        ),
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            time = "14.00",
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni"
        ),
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            time = "14.00",
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni"
        ),
        EventItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Event Yummy",
            price = 999999,
            rating = 4.5,
            time = "14.00",
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni"
        ),
        EventItem(
            imgRes = R.drawable.img_reogponorogo,
            title = "Wayang Kulit",
            price = 90000,
            rating = 4.5,
            time = "14.00",
            location = "Surakarta",
            isFavorite = false,
            category = "Pertunjukan Seni"
        ),
        EventItem(
            title = "Tari Saman",
            imgRes = R.drawable.img_event,
            rating = 4.8,
            location = "Yogyakarta",
            time = "13:00 WIB",
            price = 20000,
            isFavorite = false,
            category = "Pertunjukan Seni"
        ),
        EventItem(
            title = "Tari Tor-tor",
            imgRes = R.drawable.img_event,
            rating = 4.4,
            location = "Purwokerto",
            time = "09:00 WIB",
            price = 35000,
            isFavorite = false,
            category = "Pertunjukan Seni"
        )
    )
    val tourItemLists = listOf(
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false
        ),
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Yogyakarta",
            isFavorite = false
        ),
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Yogyakarta",
            isFavorite = false
        ),
        TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = "Tour Yummy",
            price = 999999,
            rating = 4.5,
            location = "Surakarta",
            isFavorite = false
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