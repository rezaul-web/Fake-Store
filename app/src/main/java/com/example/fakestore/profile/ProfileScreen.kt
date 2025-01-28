package com.example.fakestore.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fakestore.utils.FakeStoreAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
@Composable
fun ProfileScreen(
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    navController: NavController
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDialogForDelete by remember { mutableStateOf(false) }
    val currentUserUid = firebaseAuth.currentUser?.uid
    var addressToDelete by remember { mutableStateOf<Map<String, String>?>(null) }

    // State to hold the user profile data
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var userAddress by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshAddress by remember { mutableStateOf(false) }

    // Fetch user data and addresses from Firestore
    LaunchedEffect(key1=currentUserUid, key2 = refreshAddress) {
        if (currentUserUid != null) {
            try {
                // Fetch user profile data
                val snapshot = firestore.collection("users")
                    .document(currentUserUid)
                    .get()
                    .await()

                if (snapshot.exists()) {
                    userData = snapshot.data
                } else {
                    errorMessage = "User profile not found."
                }

                // Fetch user addresses
                val addressSnapshot = firestore.collection("users")
                    .document(currentUserUid)
                    .collection("addresses")
                    .get()
                    .await()

                userAddress = addressSnapshot.documents.mapNotNull { document ->
                    val isDefault=document.getString("isDefault")
                    val addressLine = document.getString("addressLine")
                    val city = document.getString("city")
                    val state = document.getString("state")
                    val postalCode = document.getString("postalCode")
                    val country = document.getString("country")

                    if (addressLine != null && city != null && state != null && postalCode != null && country != null) {
                        mapOf(
                            "isDefault" to isDefault!!,
                            "addressLine" to addressLine,
                            "city" to city,
                            "state" to state,
                            "postalCode" to postalCode,
                            "country" to country
                        )
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.message}"
            }
        } else {
            errorMessage = "No user is currently logged in."
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Align content to the top for better flow
    ) {
        if (userData != null) {
            Text(
                text = "Profile Screen",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),

            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Full Name: ${userData?.get("fullName") ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                    Text("Email: ${userData?.get("email") ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (userAddress.isNotEmpty()) {
                Text(
                    "Addresses:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                userAddress.forEach { address ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Address Line: ${address["addressLine"] ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                val isDefault = address["isDefault"]
                                val text = if (isDefault == "true") "Default" else ""
                                Text(text = text, color = Color.Red)
                            }
                            Text("City: ${address["city"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                            Text("State: ${address["state"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Postal Code: ${address["postalCode"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Country: ${address["country"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        navController.navigate(
                                            "updateAddress/${address["addressLine"]}/${address["city"]}/${address["state"]}/${address["postalCode"]}/${address["country"]}/${address["isDefault"]}"
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Update Address")
                                }

                                OutlinedButton(
                                    onClick = {
                                        addressToDelete = address // Set the address to delete
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete Address")
                                }
                            }
                        }
                    }
                }

// Show the delete dialog only if an address is selected
                if (addressToDelete != null) {
                    FakeStoreAlertDialog(
                        onConfirmation = {
                            if (currentUserUid != null) {
                                firestore.collection("users")
                                    .document(currentUserUid)
                                    .collection("addresses")
                                    .whereEqualTo("addressLine", addressToDelete!!["addressLine"])
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        if (querySnapshot.documents.isNotEmpty()) {
                                            val documentId = querySnapshot.documents[0].id
                                            firestore.collection("users")
                                                .document(currentUserUid)
                                                .collection("addresses")
                                                .document(documentId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    refreshAddress = !refreshAddress
                                                }
                                                .addOnFailureListener {
                                                    errorMessage = "Failed to delete address: ${it.message}"
                                                }
                                        } else {
                                            errorMessage = "Address not found for deletion."
                                        }
                                    }
                                    .addOnFailureListener {
                                        errorMessage = "Failed to query address: ${it.message}"
                                    }
                            } else {
                                errorMessage = "User is not logged in."
                            }
                            addressToDelete = null // Close the dialog
                        },
                        onDismissRequest = {
                            addressToDelete = null // Close the dialog
                        },
                        dialogTitle = "Delete Address",
                        dialogText = "You are about to delete this address."
                    )
                }
            } else {
                Text(
                    "No address found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign Out", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "An unknown error occurred.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.navigate("address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add Address")
        }
    }

    if (showDialog) {
        FakeStoreAlertDialog(
            onConfirmation = {
                firebaseAuth.signOut()
                navController.navigate("log_in")
                showDialog = false
            },
            onDismissRequest = {
                showDialog = false
            },
            dialogTitle = "Sign Out",
            dialogText = "You Are about to Sign Out"
        )
    }



}


