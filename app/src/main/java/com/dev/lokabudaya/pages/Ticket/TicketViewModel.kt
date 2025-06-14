package com.dev.lokabudaya.pages.Ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.MidtransStatusResponse
import com.dev.lokabudaya.data.OrderData
import com.dev.lokabudaya.data.PaymentTicketOrder
import com.dev.lokabudaya.data.TicketDataEvent
import com.dev.lokabudaya.data.TicketDataTour
import com.dev.lokabudaya.data.TicketOrder
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.network.MidtransAPI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.format.DateTimeFormatter

class TicketViewModel : ViewModel() {
    private val _selectedTicketOrders = MutableStateFlow<List<PaymentTicketOrder>>(emptyList())
    val selectedTicketOrders = _selectedTicketOrders.asStateFlow()

    private val _selectedEventItem = MutableStateFlow<EventItem?>(null)
    val selectedEventItem = _selectedEventItem.asStateFlow()

    private val _selectedTourItem = MutableStateFlow<TourItem?>(null)
    val selectedTourItem = _selectedTourItem.asStateFlow()

    private val _userTickets = MutableStateFlow<List<TicketDataEvent>>(emptyList())
    val userTickets = _userTickets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userOrders = MutableStateFlow<List<OrderData>>(emptyList())
    val userOrders = _userOrders.asStateFlow()

    private val _paidOrders = MutableStateFlow<List<OrderData>>(emptyList())
    val paidOrders = _paidOrders.asStateFlow()

    init {
        loadUserTickets()
    }

    fun saveOrderBeforePayment(
        eventItem: EventItem?,
        tourItem: TourItem?,
        ticketOrders: List<PaymentTicketOrder>,
        totalAmount: Int,
        snapToken: String,
        paymentUrl: String,
        orderId: String,
        onSuccess: (String) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(Exception("User not authenticated"))
            return
        }

        viewModelScope.launch {
            try {

                val orderData = when {
                    eventItem != null -> OrderData(
                        orderId = orderId,
                        eventId = eventItem.id,
                        eventTitle = eventItem.title,
                        eventImageRes = eventItem.imgRes,
                        eventImageUrl = eventItem.imageUrl,
                        eventLocation = eventItem.location,
                        eventStartDate = eventItem.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        eventTime = eventItem.eventTime,
                        ticketOrders = ticketOrders,
                        totalAmount = totalAmount,
                        totalQuantity = ticketOrders.sumOf { it.quantity },
                        status = "pending",
                        userId = currentUser.uid,
                        snapToken = snapToken,
                        paymentUrl = paymentUrl
                    )
                    tourItem != null -> OrderData(
                        orderId = orderId,
                        eventId = tourItem.id,
                        eventTitle = tourItem.title,
                        eventImageRes = tourItem.imgRes,
                        eventImageUrl = tourItem.imageUrl,
                        eventLocation = tourItem.location,
                        eventStartDate = "",
                        eventTime = tourItem.time,
                        ticketOrders = ticketOrders,
                        totalAmount = totalAmount,
                        totalQuantity = ticketOrders.sumOf { it.quantity },
                        status = "pending",
                        userId = currentUser.uid,
                        snapToken = snapToken,
                        paymentUrl = paymentUrl
                    )
                    else -> throw Exception("No event or tour item provided")
                }

                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .document(orderData.id)
                    .set(orderData)
                    .await()

                android.util.Log.d("TicketViewModel", "Order saved: ${orderData.orderId}")
                loadUserOrders()
                onSuccess(orderData.id)

            } catch (e: Exception) {
                android.util.Log.e("TicketViewModel", "Save order error: ${e.message}")
                onError(e)
            }
        }
    }

    fun loadPaidOrders() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                android.util.Log.d("TicketViewModel", "Loading paid orders...")

                val ordersSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .whereEqualTo("status", "paid") // FILTER: Hanya status paid
                    .orderBy("orderDate", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val paidOrdersList = ordersSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(OrderData::class.java)
                }

                _paidOrders.value = paidOrdersList
                android.util.Log.d("TicketViewModel", "Loaded ${paidOrdersList.size} paid orders")

            } catch (e: Exception) {
                android.util.Log.e("TicketViewModel", "Load paid orders error: ${e.message}")
                _paidOrders.value = emptyList()
            }
        }
    }

    fun checkAndUpdateOrderStatus(orderId: String) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://midtrans-api-lokabudaya.vercel.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val midtransAPI = retrofit.create(MidtransAPI::class.java)

                midtransAPI.checkTransactionStatus(orderId).enqueue(object :
                    Callback<MidtransStatusResponse> {
                    override fun onResponse(call: Call<MidtransStatusResponse>, response: Response<MidtransStatusResponse>) {

                        if (response.isSuccessful) {
                            val statusResponse = response.body()

                            if (statusResponse?.success == true && statusResponse.data != null) {
                                val transactionStatus = statusResponse.data.transaction_status


                                val newStatus = when (transactionStatus) {
                                    "capture", "settlement" -> "paid"
                                    "pending" -> "pending"
                                    "deny", "cancel", "expire", "failure" -> "expired"
                                    else -> "pending"
                                }

                                val currentOrder = _userOrders.value.find { it.orderId == orderId }
                                if (currentOrder != null && currentOrder.status != newStatus) {
                                    updateOrderStatusByTransactionId(orderId, newStatus)
                                } else {
                                }
                            } else {
                            }
                        } else {
                        }
                    }

                    override fun onFailure(call: Call<MidtransStatusResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateOrderStatusByTransactionId(transactionId: String, status: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val ordersSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .whereEqualTo("orderId", transactionId)
                    .get()
                    .await()

                if (!ordersSnapshot.isEmpty) {
                    ordersSnapshot.documents.forEach { doc ->
                        doc.reference.update("status", status).await()
                    }
                    loadUserOrders()
                } else {
                }

            } catch (e: Exception) {
            }
        }
    }

    fun autoCheckPendingOrdersStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val pendingOrders = _userOrders.value.filter { it.status == "pending" }


                pendingOrders.forEach { order ->
                    delay(1000)
                    checkAndUpdateOrderStatus(order.orderId)
                }

            } catch (_: Exception) {
            }
        }
    }

    fun autoUpdateRecentPendingOrders(timeThreshold: Long) {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val ordersSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .whereEqualTo("status", "pending")
                    .whereGreaterThan("orderDate", timeThreshold)
                    .get()
                    .await()

                if (!ordersSnapshot.isEmpty) {
                    ordersSnapshot.documents.forEach { doc ->
                        val order = doc.toObject(OrderData::class.java)
                        if (order != null) {
                            doc.reference.update("status", "paid").await()
                        }
                    }

                    loadUserOrders()
                }

            } catch (_: Exception) {
            }
        }
    }

    private fun loadUserOrders() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val ordersSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .orderBy("orderDate", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val orders = ordersSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(OrderData::class.java)
                }

                _userOrders.value = orders
                syncOrderStatusWithMidtrans()

            } catch (e: Exception) {
                _userOrders.value = emptyList()
            }
        }
    }

    // Di TicketViewModel
    fun updateOrderStatusByOrderId(orderId: String, status: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return
        }

        viewModelScope.launch {
            try {
                val ordersSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .whereEqualTo("orderId", orderId)
                    .get()
                    .await()

                if (ordersSnapshot.isEmpty) {
                    return@launch
                }


                ordersSnapshot.documents.forEach { doc ->
                    doc.reference.update("status", status).await()
                }

                // Refresh orders setelah update
                loadUserOrders()

            } catch (e: Exception) {
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .document(orderId)
                    .update("status", status)
                    .await()

                loadUserOrders()

            } catch (_: Exception) {
            }
        }
    }

    fun syncOrderStatusWithMidtrans() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val pendingOrders = _userOrders.value.filter { it.status == "pending" }


                pendingOrders.forEach { order ->
                    delay(1000)
                    checkAndUpdateOrderStatus(order.orderId)
                }

            } catch (_: Exception) {
            }
        }
    }

    fun refreshOrders() {
        loadUserOrders()
    }

    fun updateTicketOrdersEvent(orders: List<TicketOrder>, eventItem: EventItem) {
        val paymentOrders = orders
            .filter { it.quantity > 0 }
            .map { order ->
                PaymentTicketOrder(
                    ticketTypeName = order.ticketType.name,
                    quantity = order.quantity,
                    price = order.ticketType.price,
                    totalPrice = order.totalPrice
                )
            }

        _selectedTicketOrders.value = paymentOrders
        _selectedEventItem.value = eventItem
    }

    fun updateTicketOrdersTour(orders: List<TicketOrder>, tourItem: TourItem) {
            val paymentOrders = orders
                .filter { it.quantity > 0 }
                .map { order ->
                    PaymentTicketOrder(
                        ticketTypeName = order.ticketType.name,
                        quantity = order.quantity,
                        price = order.ticketType.price,
                        totalPrice = order.totalPrice
                    )
                }
            _selectedEventItem.value = null
            _selectedTicketOrders.value = paymentOrders
            _selectedTourItem.value = tourItem
        }

    fun saveTicketAfterPaymentEvent(
        eventItem: EventItem,
        ticketOrders: List<PaymentTicketOrder>,
        totalAmount: Int,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(Exception("User not authenticated"))
            return
        }

        viewModelScope.launch {
            try {
                val ticketDataEvent = TicketDataEvent(
                    eventId = eventItem.id,
                    eventTitle = eventItem.title,
                    eventImageRes = eventItem.imgRes,
                    eventLocation = eventItem.location,
                    eventStartDate = eventItem.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    eventTime = eventItem.eventTime,
                    ticketOrders = ticketOrders,
                    totalAmount = totalAmount,
                    totalQuantity = ticketOrders.sumOf { it.quantity },
                    userId = currentUser.uid
                )

                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("tickets")
                    .document(ticketDataEvent.id)
                    .set(ticketDataEvent)
                    .await()

                refreshTicketsQuietly()
                onSuccess()

            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun saveTicketAfterPaymentTour(
            tourItem: TourItem,
            ticketOrders: List<PaymentTicketOrder>,
            totalAmount: Int,
            onSuccess: () -> Unit = {},
            onError: (Exception) -> Unit = {}
        ) {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError(Exception("User not authenticated"))
                return
            }

            viewModelScope.launch {
                try {
                    val ticketDataTour = TicketDataTour(
                        tourId = tourItem.id,
                        tourTitle = tourItem.title,
                        tourImageRes = tourItem.imgRes,
                        tourLocation = tourItem.location,
                        tourTime = tourItem.time,
                        ticketOrders = ticketOrders,
                        totalAmount = totalAmount,
                        totalQuantity = ticketOrders.sumOf { it.quantity },
                        userId = currentUser.uid
                    )

                    firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("tickets")
                        .document(ticketDataTour.id)
                        .set(ticketDataTour)
                        .await()

                    refreshTicketsQuietly()
                    onSuccess()

                } catch (e: Exception) {
                    onError(e)
                }
            }
        }

    private fun refreshTicketsQuietly() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val ticketsSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("tickets")
                    .orderBy("purchaseDate", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val tickets = ticketsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketDataEvent::class.java)
                }

                _userTickets.value = tickets

            } catch (_: Exception) {
            }
        }
    }

    private fun loadUserTickets() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val ticketsSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("tickets")
                    .orderBy("purchaseDate", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val tickets = ticketsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketDataEvent::class.java)
                }

                _userTickets.value = tickets

            } catch (e: Exception) {
                _userTickets.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTopThreeTickets(): List<TicketDataEvent> {
        return _userTickets.value.take(3)
    }

    fun refreshTickets() {
        loadUserTickets()
        loadPaidOrders()
    }
}