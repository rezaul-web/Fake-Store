package com.example.fakestore.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fakestore.mainapp.Route
import com.example.fakestore.model.UserAddress
import com.example.fakestore.utils.FakeStoreTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun UpdateAddressScreen(
    navController: NavController,
    isDefault:Boolean,
    addressLine: String,
    city: String,
    state: String,
    postalCode: String,
    country: String,
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid,
    viewModel: ProfileViewModel= hiltViewModel()
) {
    var isDefault1 by remember { mutableStateOf(isDefault) }
    var newAddressLine by remember { mutableStateOf(addressLine) }
    var newCity by remember { mutableStateOf(city) }
    var newState by remember { mutableStateOf(state) }
    var newPostalCode by remember { mutableStateOf(postalCode) }
    var newCountry by remember { mutableStateOf(country) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isSelected by remember { mutableStateOf(isDefault1) }
        Text("Update Address", style = MaterialTheme.typography.titleLarge)
        if (errorMessage != null) {
            Text(errorMessage ?: "An error occurred.", color = MaterialTheme.colorScheme.error)
        }
        FakeStoreTextField(
            value = newAddressLine,
            onValueChange = { newAddressLine = it },
            label = { Text("Address Line") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = newCity,
            onValueChange = { newCity = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = newState,
            onValueChange = { newState = it },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = newPostalCode,
            onValueChange = { newPostalCode = it },
            label = { Text("Postal Code") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = newCountry,
            onValueChange = { newCountry = it },
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Set as Default")
            RadioButton(onClick = {isDefault1=!isDefault1}, selected = isDefault1)
        }
        OutlinedButton (
            onClick = {

                if (currentUserUid != null) {
                    isLoading = true
                    if (isDefault1) {
                        viewModel.updateDefaultAddress(UserAddress(isDefault1, newAddressLine, newCity, newState, newPostalCode, newCountry))
                        // Set all other addresses to non-default

                    }
                    val updatedAddress = mapOf(
                        "isDefault" to isDefault1.toString(),
                        "addressLine" to newAddressLine,
                        "city" to newCity,
                        "state" to newState,
                        "postalCode" to newPostalCode,
                        "country" to newCountry
                    )
                    firestore.collection("users")
                        .document(currentUserUid)
                        .collection("addresses")
                        .whereEqualTo("addressLine", addressLine) // Query to find the specific address
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.documents.isNotEmpty()) {
                                val documentId = querySnapshot.documents[0].id
                                firestore.collection("users")
                                    .document(currentUserUid)
                                    .collection("addresses")
                                    .document(documentId)
                                    .set(updatedAddress)
                                    .addOnSuccessListener {

                                        isLoading = false
                                        navController.navigate(Route.ProfileScreen.route) {
                                            popUpTo(navController.currentDestination?.route ?: Route.HomeScreen.route) { inclusive = true }
                                        }

                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        errorMessage = "Failed to update address: ${it.message}"
                                    }
                            } else {
                                isLoading = false
                                errorMessage = "Address not found for update."
                            }
                        }
                        .addOnFailureListener {
                            isLoading = false
                            errorMessage = "Failed to query address: ${it.message}"
                        }
                } else {
                    errorMessage = "User is not logged in."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Save Address")
            }
        }
    }
}
