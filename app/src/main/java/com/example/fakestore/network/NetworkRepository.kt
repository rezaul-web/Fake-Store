package com.example.fakestore.network

import com.example.fakestore.model.AllProducts
import com.example.fakestore.model.Category
import com.example.fakestore.model.ProductItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NetworkRepository @Inject constructor(private val fakeStoreApi: FakeStoreApi) {

    fun getAllProducts(): Flow<Resource<AllProducts>> = flow {
        emit(Resource.Loading) // Emit loading state

        try {
            val products = fakeStoreApi.getAllProducts()
            emit(Resource.Success(products)) // Emit success with data
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 4xx, 5xx responses)
            val errorMessage = "Server error: ${e.message()}"
            emit(Resource.Error(errorMessage, e))
        } catch (e: IOException) {
            // Handle network errors
            val errorMessage = "Network error: Check your internet connection."
            emit(Resource.Error(errorMessage, e))
        } catch (e: Exception) {
            // Handle unexpected errors
            val errorMessage = "Unexpected error occurred: ${e.localizedMessage}"
            emit(Resource.Error(errorMessage, e))
        }
    }
    fun getAllProductById(id:Int): Flow<Resource<ProductItem>> = flow {
        emit(Resource.Loading) // Emit loading state

        try {
            val products = fakeStoreApi.getProductById(id)
            emit(Resource.Success(products)) // Emit success with data
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 4xx, 5xx responses)
            val errorMessage = "Server error: ${e.message()}"
            emit(Resource.Error(errorMessage, e))
        } catch (e: IOException) {
            // Handle network errors
            val errorMessage = "Network error: Check your internet connection."
            emit(Resource.Error(errorMessage, e))
        } catch (e: Exception) {
            // Handle unexpected errors
            val errorMessage = "Unexpected error occurred: ${e.localizedMessage}"
            emit(Resource.Error(errorMessage, e))
        }
    }
    fun categoriesByName(name:String): Flow<Resource<AllProducts>> = flow {
        emit(Resource.Loading) // Emit loading state

        try {
            val products = fakeStoreApi.getCategoryByName(name)
            emit(Resource.Success(products)) // Emit success with data
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 4xx, 5xx responses)
            val errorMessage = "Server error: ${e.message()}"
            emit(Resource.Error(errorMessage, e))
        } catch (e: IOException) {
            // Handle network errors
            val errorMessage = "Network error: Check your internet connection."
            emit(Resource.Error(errorMessage, e))
        } catch (e: Exception) {
            // Handle unexpected errors
            val errorMessage = "Unexpected error occurred: ${e.localizedMessage}"
            emit(Resource.Error(errorMessage, e))
        }
    }
    fun getAllCategories(): Flow<Resource<Category>> = flow {
        emit(Resource.Loading) // Emit loading state

        try {
            val category = fakeStoreApi.getCategory()
            emit(Resource.Success(category)) // Emit success with data
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 4xx, 5xx responses)
            val errorMessage = "Server error: ${e.message()}"
            emit(Resource.Error(errorMessage, e))
        } catch (e: IOException) {
            // Handle network errors
            val errorMessage = "Network error: Check your internet connection."
            emit(Resource.Error(errorMessage, e))
        } catch (e: Exception) {
            // Handle unexpected errors
            val errorMessage = "Unexpected error occurred: ${e.localizedMessage}"
            emit(Resource.Error(errorMessage, e))
        }
    }



}


sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
   data object Idle:Resource<Nothing>()
}
