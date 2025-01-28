package com.example.fakestore.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.model.UserAddress
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
                if (address.isDefault) {
                   updateDefaultAddress(address)
                }
                // Check if the address already exists based on a unique combination of fields (e.g., addressLine, postalCode)
                val querySnapshot = addressesRef
                    .whereEqualTo("addressLine", address.addressLine)
                    .whereEqualTo("postalCode", address.postalCode)
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    // Address doesn't exist, so add it
                    val addressData = mapOf(
                        "isDefault" to address.isDefault.toString(),
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
                        "isDefault" to address.isDefault.toString(),
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
    fun updateDefaultAddress(address: UserAddress) {
        if (uuid == null) return

        viewModelScope.launch {
            try {
                val addressesRef = firestore.collection("users")
                    .document(uuid)
                    .collection("addresses")

                // Set all other addresses to non-default
                val defaultAddresses = addressesRef.whereEqualTo("isDefault", "true").get().await()
                for (document in defaultAddresses.documents) {
                    addressesRef.document(document.id).update("isDefault", "false").await()
                }

                // Set the new default address
                val querySnapshot = addressesRef
                    .whereEqualTo("addressLine", address.addressLine)
                    .whereEqualTo("postalCode", address.postalCode)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    val documentId = querySnapshot.documents.first().id
                    addressesRef.document(documentId).update("isDefault", "true")
                        .addOnSuccessListener {
                            Log.d("Address", "Default address updated successfully.")
                        }
                        .addOnFailureListener {
                            Log.e("Address", "Failed to update default address: ${it.message}")
                        }
                } else {
                    Log.e("Address", "Address not found to set as default.")
                }
            } catch (e: Exception) {
                Log.e("Address", "Unexpected error: ${e.message}")
            }
        }
    }


    fun updateAddress(
        isDefault: Boolean,
        addressLine: String,
        city: String,
        state: String,
        postalCode: String,
        country: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        currentUserUid:String?
    ) {
        if (currentUserUid == null) {
            onFailure("User is not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                val updatedAddress = mapOf(
                    "isDefault" to isDefault,
                    "addressLine" to addressLine,
                    "city" to city,
                    "state" to state,
                    "postalCode" to postalCode,
                    "country" to country
                )

                val addressesCollection = firestore.collection("users")
                    .document(currentUserUid)
                    .collection("addresses")

                val querySnapshot = addressesCollection
                    .whereEqualTo("addressLine", addressLine)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    val documentId = querySnapshot.documents[0].id

                    if (isDefault) {
                        // Set other addresses to non-default
                        val allAddresses = addressesCollection
                            .whereEqualTo("isDefault", true)
                            .get()
                            .await()

                        for (doc in allAddresses.documents) {
                            doc.reference.update("isDefault", false).await()
                        }
                    }

                    // Update the address
                    addressesCollection.document(documentId).set(updatedAddress).await()
                    onSuccess()
                } else {
                    onFailure("Address not found for update.")
                }
            } catch (e: Exception) {
                onFailure("Failed to update address: ${e.message}")
            }
        }
    }

    fun makeSingleAddressDefault() {
        if (uuid == null) {

            return
        }

        viewModelScope.launch {
            try {
                val addressesCollection = firestore.collection("users")
                    .document(uuid)
                    .collection("addresses")

                val querySnapshot = addressesCollection.get().await()

                if (querySnapshot.documents.size == 1) {
                    val documentId = querySnapshot.documents[0].id

                    // Update the only address to be default
                    addressesCollection.document(documentId).update("isDefault", true).await()
                    Log.d("Address", "Only address made default.")

                } else {

                }
            } catch (e: Exception) {

            }
        }
    }
}





