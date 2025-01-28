package com.example.fakestore.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.models.allproducts.UserAddress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val uuid: String? = firebaseAuth.currentUser?.uid

    fun saveUserAddress(address: UserAddress) {
        if (uuid == null) return

        viewModelScope.launch {
            try {
                val addressesRef = firestore.collection("users")
                    .document(uuid)
                    .collection("addresses")

                // Check if the address already exists based on a unique combination of fields (e.g., addressLine, postalCode)
                val querySnapshot = addressesRef
                    .whereEqualTo("addressLine", address.addressLine)
                    .whereEqualTo("postalCode", address.postalCode)
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    // Address doesn't exist, so add it
                    val addressData = mapOf(
                        "addressLine" to address.addressLine,
                        "city" to address.city,
                        "state" to address.state,
                        "postalCode" to address.postalCode,
                        "country" to address.country
                    )
                    addressesRef.add(addressData)
                        .addOnSuccessListener {
                          Log.d("address","$addressData")
                        }
                        .addOnFailureListener {
                            // Handle failure (e.g., show an error message)
                        }
                } else {
                    // Address exists, update it (optional, only if you want to change an existing address)
                    val documentId = querySnapshot.documents.first().id
                    val updatedAddressData = mapOf(
                        "addressLine" to address.addressLine,
                        "city" to address.city,
                        "state" to address.state,
                        "postalCode" to address.postalCode,
                        "country" to address.country
                    )
                    addressesRef.document(documentId).update(updatedAddressData)
                        .addOnSuccessListener {
                            // Handle success (e.g., show a confirmation message)
                        }
                        .addOnFailureListener {
                            // Handle failure (e.g., show an error message)
                        }
                }
            } catch (e: Exception) {
                // Handle unexpected errors
            }
        }
    }


}
