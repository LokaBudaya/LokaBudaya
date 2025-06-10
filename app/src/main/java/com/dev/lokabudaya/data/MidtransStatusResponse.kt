package com.dev.lokabudaya.data

data class MidtransStatusResponse(
    val success: Boolean,
    val data: TransactionStatusData? = null,
    val error: String? = null
)

data class TransactionStatusData(
    val status_code: String,
    val transaction_id: String,
    val gross_amount: String,
    val currency: String,
    val order_id: String,
    val payment_type: String,
    val signature_key: String,
    val transaction_status: String,
    val fraud_status: String? = null,
    val status_message: String,
    val merchant_id: String,
    val transaction_time: String,
    val settlement_time: String? = null,
    val expiry_time: String,
    val channel_response_code: String? = null,
    val channel_response_message: String? = null,
    val bank: String? = null,
    val approval_code: String? = null,
    val masked_card: String? = null,
    val card_type: String? = null
)