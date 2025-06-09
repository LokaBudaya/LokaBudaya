package com.dev.lokabudaya

import android.app.Application
import android.content.Intent
import android.util.Log
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        SdkUIFlowBuilder.init()
            .setClientKey("Mid-client-hPZlSLFW1FBSHSBj")
            .setContext(this)
            .setTransactionFinishedCallback { result ->
                handleTransactionResult(result)
            }
            .setMerchantBaseUrl("https://midtrans-api-lokabudaya.vercel.app/")
            .enableLog(true)
            .buildSDK()
    }

    private fun handleTransactionResult(result: TransactionResult) {
        Log.d("Midtrans", "Transaction result: ${result.response}")

        when (result.response?.transactionStatus) {
            "capture", "settlement" -> {
                Log.d("Midtrans", "Payment Success: ${result.response}")

                // PERBAIKAN: Broadcast success dengan order_id
                val intent = Intent("MIDTRANS_PAYMENT_SUCCESS")
                intent.putExtra("order_id", result.response?.transactionId)
                intent.putExtra("transaction_id", result.response?.transactionId)
                sendBroadcast(intent)
            }
            "pending" -> {
                Log.d("Midtrans", "Payment Pending: ${result.response}")

                val intent = Intent("MIDTRANS_PAYMENT_PENDING")
                intent.putExtra("order_id", result.response?.transactionId)
                sendBroadcast(intent)
            }
            "deny", "cancel", "expire" -> {
                Log.d("Midtrans", "Payment Failed: ${result.response}")

                val intent = Intent("MIDTRANS_PAYMENT_FAILED")
                intent.putExtra("order_id", result.response?.transactionId)
                sendBroadcast(intent)
            }
        }
    }
}