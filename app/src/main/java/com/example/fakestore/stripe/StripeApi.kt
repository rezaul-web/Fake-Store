package com.example.fakestore.stripe

import com.example.fakestore.model.PaymentRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface StripeApi {

    @POST("/payment-sheet")
    suspend fun getDetails(@Body request: PaymentRequest): StripeResponse
}
