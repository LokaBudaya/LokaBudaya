package com.dev.lokabudaya.network

import com.dev.lokabudaya.data.MidtransRequest
import com.dev.lokabudaya.data.MidtransResponse
import com.dev.lokabudaya.data.MidtransStatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MidtransAPI {
    @POST("api/payment")
    fun createTransaction(@Body request: MidtransRequest): Call<MidtransResponse>

    @GET("api/check-status")
    fun checkTransactionStatus(@Query("order_id") orderId: String): Call<MidtransStatusResponse>
}