package com.example.fakestore.stripepay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class StripeViewModel @Inject constructor(
    private val stripePay: StripePay,
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    lateinit var ephemeralKey: String
    lateinit var customerId: String
    lateinit var clientSecret: String

    fun createCustomer() {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            try {
                val customerId = stripePay.createCustomer().id


               createEphemeralKey(customerId)
                createPaymentIntent(customerId = customerId)
            } catch (e: HttpException) {
                _paymentState.value = PaymentState.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error("Network error: ${e.message}")
            }
        }
    }

    private fun createEphemeralKey(customerId: String) {
        viewModelScope.launch {
            try {
                val ephemeralKey = stripePay.createEphemeralKey(customerId)
                // Pass the customerId (not the ephemeral key ID) to createPaymentIntent
                createPaymentIntent(customerId)
            } catch (e: HttpException) {
                _paymentState.value = PaymentState.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error("Network error: ${e.message}")
            }
        }
    }

    private fun createPaymentIntent(customerId: String, amount: Int = 5000, currency: String = "inr") {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            try {
                val response = stripePay.createPaymentIntent(customerId, amount, currency)

                if (response.isSuccessful) {
                    val paymentIntent = response.body()
                    paymentIntent?.let {
                        clientSecret = it.client_secret
                        _paymentState.value = PaymentState.Success(clientSecret)
                    } ?: run {
                        _paymentState.value = PaymentState.Error("Payment intent response is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _paymentState.value = PaymentState.Error("Failed to create payment intent: $errorBody")
                }
            } catch (e: HttpException) {
                _paymentState.value = PaymentState.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error("Network error: ${e.message}")
            }
        }
    }
    

}

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Loading : PaymentState()
    data class Success(val clientSecret: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

