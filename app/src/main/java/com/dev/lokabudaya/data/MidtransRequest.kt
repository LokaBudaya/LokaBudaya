package com.dev.lokabudaya.data

data class MidtransRequest(
    val order_id: String,
    val gross_amount: Int,
    val item_details: List<ItemDetail>,
    val customer_details: CustomerDetail
)

data class ItemDetail(
    val id: String,
    val price: Int,
    val quantity: Int,
    val name: String
)

data class CustomerDetail(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String
)

data class MidtransResponse(
    val success: Boolean,
    val token: String,
    val redirect_url: String,
    val error: String? = null
)