package com.dev.lokabudaya.network

import com.dev.lokabudaya.data.MidtransRequest
import com.dev.lokabudaya.data.MidtransResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MidtransAPI {
    @POST("api/payment")
    fun createTransaction(@Body request: MidtransRequest): Call<MidtransResponse>
}