package com.example.fakestore.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String

)