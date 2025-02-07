package com.example.fakestore.stripe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.model.PaymentRequest
import com.example.fakestore.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class StripeViewModel @Inject constructor(private val stripeApi: StripeApi) : ViewModel() {

    private val _stripeResponse = MutableStateFlow<Resource<StripeResponse>>(Resource.Idle)
    val stripeResponse = _stripeResponse.asStateFlow()

    fun getUser(amount: String) {
        _stripeResponse.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = stripeApi.getDetails(PaymentRequest(amount = amount))
                _stripeResponse.value = Resource.Success(response) // Set success response
            } catch (e: HttpException) { // API errors (4xx, 5xx)
                _stripeResponse.value = Resource.Error("Server Error: ${e.response()?.errorBody()?.string() ?: "Unknown error"}")
            } catch (io: IOException) { // Network errors (No Internet, Timeout, etc.)
                _stripeResponse.value = Resource.Error("Network Error: ${io.localizedMessage ?: "Check your connection"}")
            } catch (e: Exception) { // Other unexpected errors
                _stripeResponse.value = Resource.Error("Unexpected Error: ${e.localizedMessage ?: "Something went wrong"}")
            }
        }
    }
}


