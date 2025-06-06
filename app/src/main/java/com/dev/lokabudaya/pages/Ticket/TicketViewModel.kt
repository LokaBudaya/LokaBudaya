package com.dev.lokabudaya.pages.Ticket

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.OrderData
import com.dev.lokabudaya.data.PaymentTicketOrder
import com.dev.lokabudaya.data.TicketDataEvent
import com.dev.lokabudaya.data.TicketDataTour
import com.dev.lokabudaya.data.TicketOrder
import com.dev.lokabudaya.data.TourItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
                val orderId = "ORDER-${System.currentTimeMillis()}"

                val orderData = when {
                    eventItem != null -> OrderData(
                        orderId = orderId,
                        eventId = eventItem.id,
                        eventTitle = eventItem.title,
                        eventImageRes = eventItem.imgRes,
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
                        eventLocation = tourItem.location,
                        eventStartDate = "", // Tour tidak punya start date
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

                // Save ke Firestore collection orders
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
                android.util.Log.d("TicketViewModel", "Loaded ${orders.size} orders")

            } catch (e: Exception) {
                android.util.Log.e("TicketViewModel", "Load orders error: ${e.message}")
                _userOrders.value = emptyList()
            }
        }
    }

    // Di TicketViewModel
    fun updateOrderStatusByOrderId(orderId: String, status: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                // Find order dengan orderId dan update status
                val ordersSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("orders")
                    .whereEqualTo("orderId", orderId)
                    .get()
                    .await()

                ordersSnapshot.documents.forEach { doc ->
                    doc.reference.update("status", status).await()
                }

                loadUserOrders()
                Log.d("TicketViewModel", "Order status updated: $orderId -> $status")

            } catch (e: Exception) {
                Log.e("TicketViewModel", "Update order status error: ${e.message}")
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
                android.util.Log.d("TicketViewModel", "Order status updated: $orderId -> $status")

            } catch (e: Exception) {
                android.util.Log.e("TicketViewModel", "Update order status error: ${e.message}")
            }
        }
    }

    // TAMBAHKAN: Function untuk refresh orders
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
                // PERBAIKAN: Tidak set loading state di sini karena sudah di-handle di UI
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

                // Save ke Firestore
                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("tickets")
                    .document(ticketDataEvent.id)
                    .set(ticketDataEvent)
                    .await()

                // PERBAIKAN: Refresh tickets tanpa loading state
                refreshTicketsQuietly()
                onSuccess()

            } catch (e: Exception) {
                android.util.Log.e("TicketViewModel", "Save ticket error: ${e.message}")
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
                    // PERBAIKAN: Tidak set loading state di sini karena sudah di-handle di UI
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

                    // Save ke Firestore
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("tickets")
                        .document(ticketDataTour.id)
                        .set(ticketDataTour)
                        .await()

                    // PERBAIKAN: Refresh tickets tanpa loading state
                    refreshTicketsQuietly()
                    onSuccess()

                } catch (e: Exception) {
                    android.util.Log.e("TicketViewModel", "Save ticket error: ${e.message}")
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

            } catch (e: Exception) {
                android.util.Log.e("TicketViewModel", "Refresh tickets error: ${e.message}")
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
        loadUserOrders()
    }
}