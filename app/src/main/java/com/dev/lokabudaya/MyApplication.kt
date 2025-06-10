package com.dev.lokabudaya

import android.app.Application
import android.content.Intent
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-Qfvn78dMZ9wGWnj5")
            .setContext(this)
            .setTransactionFinishedCallback { result ->
                handleTransactionResult(result)
            }
            .setMerchantBaseUrl("https://midtrans-api-lokabudaya.vercel.app/")
            .enableLog(true)
            .buildSDK()
    }

    private fun handleTransactionResult(result: TransactionResult) {

        when (result.response?.transactionStatus) {
            "capture", "settlement" -> {

                val intent = Intent("MIDTRANS_PAYMENT_SUCCESS")
                intent.putExtra("order_id", result.response?.transactionId)
                intent.putExtra("transaction_id", result.response?.transactionId)
                sendBroadcast(intent)
            }
            "pending" -> {
                val intent = Intent("MIDTRANS_PAYMENT_PENDING")
                intent.putExtra("order_id", result.response?.transactionId)
                sendBroadcast(intent)
            }
            "deny", "cancel", "expire" -> {
                val intent = Intent("MIDTRANS_PAYMENT_FAILED")
                intent.putExtra("order_id", result.response?.transactionId)
                sendBroadcast(intent)
            }
        }
    }
}