package com.example.fakestore.stripepay

import com.example.fakestore.model.Customer
import com.example.fakestore.model.EphemeralKeyResponse
import com.example.fakestore.model.PaymentIntentResponse
import com.example.fakestore.utils.Data
import retrofit2.Response
import retrofit2.http.*

interface StripePay {
    @Headers("Authorization: Bearer YOUR_SECRET_KEY")
    @POST("customers")
    suspend fun createCustomer(): Customer

    @FormUrlEncoded
    @Headers(
        "Stripe-Version: 2022-11-15", // Use the correct API version
        "Authorization: Bearer ${Data.SECRET_KEY}"
    )
    @POST("ephemeral_keys")
    suspend fun createEphemeralKey(
        @Field("customer") customerId: String // Ensure this parameter is passed
    ): EphemeralKeyResponse

    @Headers("Authorization: Bearer ${Data.SECRET_KEY}")
    @FormUrlEncoded
    @POST("payment_intents")
    suspend fun createPaymentIntent(
        @Field("customer") customerId: String,
        @Field("amount") amount: Int,
        @Field("currency") currency: String = "inr",
        @Field("automatic_payment_methods[enabled]") automatePay: Boolean = true
    ): Response<PaymentIntentResponse> // Wrap in Response<T>
}
