package com.dev.lokabudaya.data

import FirebaseRepository
import androidx.compose.ui.graphics.Color
import com.dev.lokabudaya.R
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.UUID

data class KulinerItemFirebase(
    val title: String = "",
    val desc: String = "",
    val imgRes: String = "",
    val kulinerTime: String = "",
    val price: Long = 0,
    val rating: Double = 0.0,
    val location: String = "",
    val isFavorite: Boolean = false,
    val latitude: Double = 0.0,
    val longtitude: Double = 0.0
)

data class EventItemFirebase(
    val title: String = "",
    val desc: String = "",
    val imgRes: String = "",
    val price: Long = 0,
    val rating: Double = 0.0,
    val location: String = "",
    val isFavorite: Boolean = false,
    val category: String = "",
    val latitude: Double = 0.0,
    val longtitude: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val eventTime: String = ""
)

data class TourItemFirebase(
    val title: String = "",
    val desc: String = "",
    val imgRes: String = "",
    val price: Long = 0,
    val time: String = "",
    val rating: Double = 0.0,
    val location: String = "",
    val isFavorite: Boolean = false,
    val latitude: Double = 0.0,
    val longtitude: Double = 0.0
)

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
    val kulinerTime: String,
    val price:Int,
    val rating:Double,
    val location:String,
    var isFavorite:Boolean,
    var desc:String,
    var latitude:Double,
    var longtitude:Double,
    val imageUrl: String = "",
    val label: String = "Kuliner",
    val backgroundLabelColor: Color = Color(0xFF9A5F38),
    val textLabelColor: Color = Color(0xFFFFDAC2)
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
    val startDate:LocalDate,
    val endDate:LocalDate,
//    val eventDateDay:String,
//    val eventDateMonth: String,
//    val eventDateYear: String,
    val eventTime:String,
    val imageUrl: String = "",
    val backgroundLabelColor: Color = Color(0xFF76395F),
    val textLabelColor: Color = Color(0xFFFFE8F6)
)
data class TourItem(
    val id: String = UUID.randomUUID().toString(),
    val imgRes:Int,
    val title:String,
    val time: String,
    val price:Int,
    val rating:Double,
    val location:String,
    var isFavorite:Boolean,
    var desc:String,
    var latitude:Double,
    var longtitude:Double,
    val imageUrl: String = "",
    val label:String = "Tour",
    val backgroundLabelColor: Color = Color(0xFF466F79),
    val textLabelColor: Color = Color(0xFFCCF5FF)
)
data class TicketItem(
    val id:String = UUID.randomUUID().toString().take(12),
    val title:String,
    val detailedDesc:String,
    val date:String,
    val location:String,
    val qrCode:Int,
    val image:Int
)

data class TicketType(
    val id: String,
    val name: String,
    val price: Int,
    val description: String,
    val maxQuantity: Int = 10,
    val minQuantity: Int = 0
)

data class TicketOrder(
    val ticketType: TicketType,
    var quantity: Int = 0
) {
    val totalPrice: Int
        get() = ticketType.price * quantity
}

data class TicketDataEvent(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String = "",
    val eventTitle: String = "",
    val eventImageRes: Int = 0,
    val eventLocation: String = "",
    val eventStartDate: String = "",
    val eventTime: String = "",
    val ticketOrders: List<PaymentTicketOrder> = emptyList(),
    val totalAmount: Int = 0,
    val totalQuantity: Int = 0,
    val purchaseDate: Long = System.currentTimeMillis(),
    val status: String = "active", // active, used, expired
    val userId: String = ""
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        eventId = "",
        eventTitle = "",
        eventImageRes = 0,
        eventLocation = "",
        eventStartDate = "",
        eventTime = "",
        ticketOrders = emptyList(),
        totalAmount = 0,
        totalQuantity = 0,
        purchaseDate = System.currentTimeMillis(),
        status = "active",
        userId = ""
    )
}
data class TicketDataTour(
    val id: String = UUID.randomUUID().toString(),
    val tourId: String = "",
    val tourTitle: String = "",
    val tourImageRes: Int = 0,
    val tourLocation: String = "",
    val tourStartDate: String = "",
    val tourTime: String = "",
    val ticketOrders: List<PaymentTicketOrder> = emptyList(),
    val totalAmount: Int = 0,
    val totalQuantity: Int = 0,
    val purchaseDate: Long = System.currentTimeMillis(),
    val status: String = "active", // active, used, expired
    val userId: String = ""
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        tourId = "",
        tourTitle = "",
        tourImageRes = 0,
        tourLocation = "",
        tourStartDate = "",
        tourTime = "",
        ticketOrders = emptyList(),
        totalAmount = 0,
        totalQuantity = 0,
        purchaseDate = System.currentTimeMillis(),
        status = "active",
        userId = ""
    )
}

data class PaymentTicketOrder(
    val ticketTypeName: String = "",
    val quantity: Int = 0,
    val price: Int = 0,
    val totalPrice: Int = 0
) {
    constructor() : this("", 0, 0, 0)
}

data class OrderData(
    val id: String = UUID.randomUUID().toString(),
    val orderId: String = "",
    val eventId: String = "",
    val eventTitle: String = "",
    val eventImageRes: Int = 0,
    val eventImageUrl: String = "",
    val eventLocation: String = "",
    val eventStartDate: String = "",
    val eventTime: String = "",
    val ticketOrders: List<PaymentTicketOrder> = emptyList(),
    val totalAmount: Int = 0,
    val totalQuantity: Int = 0,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "pending", // pending, paid, expired, cancelled
    val userId: String = "",
    val snapToken: String = "",
    val paymentUrl: String = ""
)

object DataProvider {
    private val firebaseRepository = FirebaseRepository()

    private var _kulinerItemLists: List<KulinerItem>? = null
    private var _eventItemLists: List<EventItem>? = null
    private var _tourItemLists: List<TourItem>? = null
    val blogCards = listOf(
        BlogCardClass(
            title = "Menjelajahi Keindahan Candi Borobudur di Pagi Hari",
            content = "Pengalaman magis menyaksikan sunrise di Candi Borobudur. Tips terbaik untuk berkunjung dan spot foto terbaik yang wajib dicoba.",
            imageId = R.drawable.img_blog_borobudur,
            date = "15 Juni 2025",
            viewers = "2.8K"
        ),
        BlogCardClass(
            title = "Festival Budaya Yogyakarta: Tradisi yang Tak Pernah Pudar",
            content = "Merasakan kemeriahan Festival Budaya Yogyakarta dengan berbagai pertunjukan seni tradisional, kuliner khas, dan workshop batik.",
            imageId = R.drawable.img_blog_festival,
            date = "12 Juni 2025",
            viewers = "1.9K"
        ),
        BlogCardClass(
            title = "Kuliner Legendaris Malioboro yang Wajib Dicoba",
            content = "Panduan lengkap kuliner khas Yogyakarta di sepanjang Jalan Malioboro. Dari gudeg hingga bakpia, semua ada di sini!",
            imageId = R.drawable.img_blog_kulinerjogja,
            date = "10 Juni 2025",
            viewers = "3.2K"
        )
    )
    val kulinerItemLists: List<KulinerItem>
        get() {
            if (_kulinerItemLists == null) {
                _kulinerItemLists = runBlocking {
                    firebaseRepository.getKulinerItems().ifEmpty {
                        getDefaultKulinerItems()
                    }
                }
            }
            return _kulinerItemLists!!
        }
    val eventItemLists: List<EventItem>
        get() {
            if (_eventItemLists == null) {
                _eventItemLists = runBlocking {
                    firebaseRepository.getEventItems().ifEmpty {
                        getDefaultEventItems()
                    }
                }
            }
            return _eventItemLists!!
        }
    val tourItemLists: List<TourItem>
        get() {
            if (_tourItemLists == null) {
                _tourItemLists = runBlocking {
                    firebaseRepository.getTourItems().ifEmpty {
                        getDefaultTourItems()
                    }
                }
            }
            return _tourItemLists!!
        }
    suspend fun refreshData() {
        _kulinerItemLists = firebaseRepository.getKulinerItems()
        _eventItemLists = firebaseRepository.getEventItems()
        _tourItemLists = firebaseRepository.getTourItems()
    }

    private fun getDefaultKulinerItems(): List<KulinerItem> {
        return listOf(
            KulinerItem(
                imgRes = R.drawable.img_bestik,
                title = "Bestik Pak Darmo",
                kulinerTime = "10 AM - 10 PM",
                price = 15000,
                rating = 4.9,
                location = "Surakarta",
                isFavorite = false,
                desc = "INI DESKRIPSI MAKANAN",
                latitude = -7.574178450295152,
                longtitude = 110.81591618151339
            )
        )
    }

    private fun getDefaultEventItems(): List<EventItem> {
        return listOf(
            // default event items
        )
    }

    private fun getDefaultTourItems(): List<TourItem> {
        return listOf(
            // default tour items
        )
    }
}