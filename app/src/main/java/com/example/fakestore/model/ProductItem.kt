package com.example.fakestore.model

data class ProductItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    var price: Double,
    val rating: Rating,
    val title: String
)