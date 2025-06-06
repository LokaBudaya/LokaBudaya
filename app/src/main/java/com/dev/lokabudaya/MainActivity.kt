package com.dev.lokabudaya

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Auth.PaymentNavigationHelper
import com.dev.lokabudaya.pages.Ticket.TicketViewModel
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.google.android.libraries.places.api.Places

class MainActivity : FragmentActivity() {

    private lateinit var paymentReceiver: BroadcastReceiver
    private lateinit var ticketViewModel: TicketViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()
        ticketViewModel = TicketViewModel()

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(
                applicationContext,
                getString(R.string.google_map_api_key)
            )
        }

        setupPaymentReceiver()

        setContent {
            LokaBudayaTheme {
                val navController = rememberNavController()

                PaymentNavigationHelper.setNavController(navController)
                MainScreen(
                    modifier = Modifier.padding(),
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupPaymentReceiver() {
        paymentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "MIDTRANS_PAYMENT_SUCCESS" -> {
                        val transactionId = intent.getStringExtra("transaction_id")
                        android.util.Log.d("MainActivity", "Transaction ID: $transactionId")
                        saveTicketAfterPaymentSuccess()
                        PaymentNavigationHelper.navigateToPaymentSuccess()
                    }
                    "MIDTRANS_PAYMENT_PENDING" -> {
                        android.util.Log.d("MainActivity", "Payment pending received")
                        // TODO: Show pending message
                    }
                    "MIDTRANS_PAYMENT_FAILED" -> {
                        android.util.Log.d("MainActivity", "Payment failed received")
                        // TODO: Show error message
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction("MIDTRANS_PAYMENT_SUCCESS")
            addAction("MIDTRANS_PAYMENT_PENDING")
            addAction("MIDTRANS_PAYMENT_FAILED")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(
                this,
                paymentReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(paymentReceiver, filter)
        }
    }

    private fun saveTicketAfterPaymentSuccess() {
        // Get current ticket data dari TicketViewModel
        val eventItem = ticketViewModel.selectedEventItem.value
        val tourItem = ticketViewModel.selectedTourItem.value
        val ticketOrders = ticketViewModel.selectedTicketOrders.value
        val totalAmount = ticketOrders.sumOf { it.totalPrice }

        val transactionId = intent.getStringExtra("transaction_id")
        if (transactionId != null) {
            // Find order by transaction ID dan update status
            ticketViewModel.updateOrderStatus(transactionId, "paid")
        }

        // TAMBAHKAN: Get order ID dari intent
        val orderId = intent.getStringExtra("order_id")

        when {
            eventItem != null -> {
                ticketViewModel.saveTicketAfterPaymentEvent(
                    eventItem = eventItem,
                    ticketOrders = ticketOrders,
                    totalAmount = totalAmount,
                    onSuccess = {
                        Log.d("MainActivity", "Event ticket saved successfully")
                        // TAMBAHKAN: Update order status
                        if (orderId != null) {
                            ticketViewModel.updateOrderStatusByOrderId(orderId, "paid")
                        }
                    },
                    onError = { error ->
                        Log.e("MainActivity", "Failed to save event ticket: ${error.message}")
                    }
                )
            }
            tourItem != null -> {
                ticketViewModel.saveTicketAfterPaymentTour(
                    tourItem = tourItem,
                    ticketOrders = ticketOrders,
                    totalAmount = totalAmount,
                    onSuccess = {
                        Log.d("MainActivity", "Tour ticket saved successfully")
                        // TAMBAHKAN: Update order status
                        if (orderId != null) {
                            ticketViewModel.updateOrderStatusByOrderId(orderId, "paid")
                        }
                    },
                    onError = { error ->
                        Log.e("MainActivity", "Failed to save tour ticket: ${error.message}")
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(paymentReceiver)
    }
}