package com.example.fakestore.ordersmanagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.model.UserAddress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    init {
        provideDefaultAddress()
    }

    private val _defaultAddress = MutableStateFlow<UserAddress?>(null)
    val defaultAddress = _defaultAddress

    private val _deliveryCharge = MutableStateFlow(50) // Example fixed charge
    val deliveryCharge: StateFlow<Int> = _deliveryCharge

    private val _otherCharges = MutableStateFlow(20) // Example tax or service fee
    val otherCharges: StateFlow<Int> = _otherCharges

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _price = MutableStateFlow(0)
    val price = _price.value

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

   private fun provideDefaultAddress() {
        viewModelScope.launch {
            try {
                val addressSnapshot = firebaseAuth.uid?.let { uid ->
                    firestore.collection("users")
                        .document(uid)
                        .collection("addresses")
                        .whereEqualTo("isDefault", "true") // Ensure this is stored as a String
                        .get()
                        .await()
                }

                if (addressSnapshot != null && !addressSnapshot.isEmpty) {
                    val addressData = addressSnapshot.documents.first().data

                    if (addressData != null) {
                        _defaultAddress.value = UserAddress(
                            isDefault = addressData["isDefault"] as? String == "true", // Convert String to Boolean
                            addressLine = addressData["addressLine"] as? String ?: "",
                            city = addressData["city"] as? String ?: "",
                            state = addressData["state"] as? String ?: "",
                            postalCode = addressData["postalCode"] as? String ?: "",
                            country = addressData["country"] as? String ?: ""
                        )
                    }
                } else {
                    _defaultAddress.value = null // No default address found
                }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching default address: ${e.message}")
            }
        }
    }

}
