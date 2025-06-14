import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.EventItemFirebase
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.data.KulinerItemFirebase
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.data.TourItemFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getEventItems(): List<EventItem> {
        return try {
            val snapshot = firestore.collection("events").get().await()

            val eventsList = snapshot.documents.mapNotNull { doc ->

                val data = doc.data
                if (data != null) {
                    val title = data["nama_event"] as? String ?: data["title"] as? String ?: ""
                    val desc = data["deskripsi"] as? String ?: data["desc"] as? String ?: ""
                    val imgRes = data["gambar_event"] as? String ?: data["imgRes"] as? String ?: ""
                    val price = (data["harga_tiket"] as? Number)?.toLong() ?: (data["price"] as? Number)?.toLong() ?: 0L
                    val rating = (data["rating"] as? Number)?.toDouble() ?: 0.0
                    val location = data["lokasi"] as? String ?: data["location"] as? String ?: ""
                    val isFavorite = data["isFavorite"] as? Boolean ?: false
                    val category = data["kategori"] as? String ?: data["category"] as? String ?: ""
                    val latitude = (data["latitude"] as? Number)?.toDouble() ?: 0.0
                    val longtitude = (data["longtitude"] as? Number)?.toDouble() ?: 0.0
                    val eventTime = data["eventTime"] as? String ?: ""

                    val startDateTimestamp = data["startDate"] as? com.google.firebase.Timestamp
                    val endDateTimestamp = data["endDate"] as? com.google.firebase.Timestamp

                    val startDate = startDateTimestamp?.toDate()?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now()
                    val endDate = endDateTimestamp?.toDate()?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now()

                    convertToEventItem(
                        title = title,
                        desc = desc,
                        imgRes = imgRes,
                        price = price,
                        rating = rating,
                        location = location,
                        isFavorite = isFavorite,
                        category = category,
                        latitude = latitude,
                        longtitude = longtitude,
                        startDate = startDate,
                        endDate = endDate,
                        eventTime = eventTime
                    )
                } else {
                    null
                }
            }

            eventsList

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun convertToEventItem(
        title: String,
        desc: String,
        imgRes: String,
        price: Long,
        rating: Double,
        location: String,
        isFavorite: Boolean,
        category: String,
        latitude: Double,
        longtitude: Double,
        startDate: LocalDate,
        endDate: LocalDate,
        eventTime: String
    ): EventItem {
        return EventItem(
            imgRes = R.drawable.img_event,
            title = title,
            price = price.toInt(),
            rating = rating,
            location = location,
            isFavorite = isFavorite,
            category = category,
            desc = desc,
            latitude = latitude,
            longtitude = longtitude,
            startDate = startDate,
            endDate = endDate,
            eventTime = eventTime,
            imageUrl = imgRes
        )
    }

    suspend fun getKulinerItems(): List<KulinerItem> {
        return try {
            val snapshot = firestore.collection("kuliners").get().await()
            snapshot.documents.mapNotNull { doc ->
                val firebaseItem = doc.toObject(KulinerItemFirebase::class.java)
                firebaseItem?.let { convertToKulinerItem(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTourItems(): List<TourItem> {
        return try {
            val snapshot = firestore.collection("tours").get().await()
            snapshot.documents.mapNotNull { doc ->
                val firebaseItem = doc.toObject(TourItemFirebase::class.java)
                firebaseItem?.let { convertToTourItem(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun convertToKulinerItem(firebase: KulinerItemFirebase): KulinerItem {
        return KulinerItem(
            imgRes = R.drawable.img_tehtarik,
            title = firebase.title,
            kulinerTime = firebase.kulinerTime,
            price = firebase.price.toInt(),
            rating = firebase.rating,
            location = firebase.location,
            isFavorite = firebase.isFavorite,
            desc = firebase.desc,
            latitude = firebase.latitude,
            longtitude = firebase.longtitude,
            imageUrl = firebase.imgRes
        )
    }

    private fun convertToEventItem(firebase: EventItemFirebase): EventItem {
        return EventItem(
            imgRes = R.drawable.img_event,
            title = firebase.title,
            price = firebase.price.toInt(),
            rating = firebase.rating,
            location = firebase.location,
            isFavorite = firebase.isFavorite,
            category = firebase.category,
            desc = firebase.desc,
            latitude = firebase.latitude,
            longtitude = firebase.longtitude,
            startDate = parseDate(firebase.startDate),
            endDate = parseDate(firebase.endDate),
            eventTime = firebase.eventTime,
            imageUrl = firebase.imgRes
        )
    }

    private fun convertToTourItem(firebase: TourItemFirebase): TourItem {
        return TourItem(
            imgRes = R.drawable.img_tehtarik,
            title = firebase.title,
            price = firebase.price.toInt(),
            time = firebase.time,
            rating = firebase.rating,
            location = firebase.location,
            isFavorite = firebase.isFavorite,
            desc = firebase.desc,
            latitude = firebase.latitude,
            longtitude = firebase.longtitude,
            imageUrl = firebase.imgRes
        )
    }

    private fun parseDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }
}