package com.dev.lokabudaya.pages.Ticket

import androidx.lifecycle.ViewModel
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.TicketOrder
import com.dev.lokabudaya.pages.Payment.PaymentTicketOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TicketViewModel : ViewModel() {
    private val _selectedTicketOrders = MutableStateFlow<List<PaymentTicketOrder>>(emptyList())
    val selectedTicketOrders = _selectedTicketOrders.asStateFlow()

    private val _selectedEventItem = MutableStateFlow<EventItem?>(null)
    val selectedEventItem = _selectedEventItem.asStateFlow()

    fun updateTicketOrders(orders: List<TicketOrder>, eventItem: EventItem) {
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
}