package com.example.fakestore.ordersmanagement

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
@HiltViewModel
class OrdersViewModel @Inject constructor() : ViewModel() {

    private val _deliveryCharge = MutableStateFlow(50) // Example fixed charge
    val deliveryCharge: StateFlow<Int> = _deliveryCharge

    private val _otherCharges = MutableStateFlow(20) // Example tax or service fee
    val otherCharges: StateFlow<Int> = _otherCharges

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

  private  val _price = MutableStateFlow(0)
    val price=_price.value

    // Update quantity
    fun updateQuantity(increment: Boolean) {
        if (increment) {
            _quantity.value++
        } else {
            if (_quantity.value > 1) {
                _quantity.value--
            }
        }
    }

    // Calculate the total price including delivery and other charges
}
