package com.example.fakestore.network

import com.example.fakestore.models.allproducts.AllProducts
import com.example.fakestore.models.allproducts.ProductItem
import retrofit2.http.GET
import retrofit2.http.Path

interface FakeStoreApi {
    @GET("products")
    suspend fun getAllProducts():AllProducts

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id:Int):ProductItem
}