package com.example.fakestore.model

data class UserAddress(
    val isDefault:Boolean=false,
    val addressLine: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)
