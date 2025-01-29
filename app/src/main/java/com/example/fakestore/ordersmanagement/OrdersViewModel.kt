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
    private val currentUserUid = firebaseAuth.currentUser?.uid
private val _defaultAddress=MutableStateFlow<UserAddress?>(null)
    val defaultAddress=_defaultAddress

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
    private fun provideDefaultAddress() {
        if (currentUserUid == null) return
        viewModelScope.launch {
            try {
                // Fetch user addresses and filter for the default address
                val addressSnapshot = firestore.collection("users")
                    .document(currentUserUid)
                    .collection("addresses")
                    .whereEqualTo("isDefault", "true") // Correctly use Boolean value here
                    .get()
                    .await()

                // Check if a default address exists
                if (!addressSnapshot.isEmpty) {
                    // Get the first default address from the result
                    val addressData = addressSnapshot.documents.first().data

                    // Map the Firestore data to a UserAddress object
                    val address = UserAddress(
                        isDefault = addressData?.get("isDefault") as? Boolean ?: false,
                        addressLine = addressData?.get("addressLine") as? String ?: "",
                        city = addressData?.get("city") as? String ?: "",
                        state = addressData?.get("state") as? String ?: "",
                        postalCode = addressData?.get("postalCode") as? String ?: "",
                        country = addressData?.get("country") as? String ?: ""
                    )

                    // Update the default address state
                    _defaultAddress.value = address
                    Log.d("DefaultAddress", "Default Address: $address")
                } else {
                    // No default address found
                    Log.d("DefaultAddress", "No default address found")
                }
            } catch (e: Exception) {
                // Handle any errors that occur during the fetching
                Log.e("Error", "Error fetching default address: ${e.message}")
            }
        }
    }



}
