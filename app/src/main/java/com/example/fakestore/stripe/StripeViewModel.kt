package com.example.fakestore.stripe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.model.PaymentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StripeViewModel @Inject constructor(private val stripeApi: StripeApi) : ViewModel() {

    // MutableStateFlow to hold the StripeResponse data, initially null
    private val _stripeResponse = MutableStateFlow<StripeResponse?>(null)
    val stripeResponse = _stripeResponse.asStateFlow()

    // Function to fetch Stripe data asynchronously
     fun getUser(amount:String) {
        viewModelScope.launch {
            try {
                // Perform the network call and update the state
                val response = stripeApi.getDetails(PaymentRequest(amount = amount))
                _stripeResponse.value = response // Set the value once the response is fetched
            } catch (e: Exception) {
                // Handle any error (log or update state with null/error)
                _stripeResponse.value = null
            }
        }
    }


}

