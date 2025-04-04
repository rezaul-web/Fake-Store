package com.example.fakestore.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fakestore.model.UserAddress
import com.example.fakestore.utils.FakeStoreTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

const val USER_ADDRESS_SCREEN = "user_address"

@Composable
fun UserAddressScreen(
    navController: NavController,
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var isSelectedDefault by remember { mutableStateOf(false) }
    var fetchedLocation by remember { mutableStateOf(false) }
    var triggerLocationFetch by remember { mutableStateOf(false) }

    val addressLine = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val state = remember { mutableStateOf("") }
    val postalCode = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        FakeStoreTextField(
            value = addressLine.value,
            onValueChange = { addressLine.value = it },
            label = { Text(text = "Address Line") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = city.value,
            onValueChange = { city.value = it },
            label = { Text(text = "City") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = state.value,
            onValueChange = { state.value = it },
            label = { Text(text = "State") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = postalCode.value,
            onValueChange = { postalCode.value = it },
            label = { Text(text = "Postal Code") },
            modifier = Modifier.fillMaxWidth()
        )
        FakeStoreTextField(
            value = country.value,
            onValueChange = { country.value = it },
            label = { Text(text = "Country") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Set as Default")
            RadioButton(
                onClick = { isSelectedDefault = !isSelectedDefault },
                selected = isSelectedDefault
            )

            Text(text = "Use Current Location")
            RadioButton(
                onClick = {
                    triggerLocationFetch = true // only fetch when clicked
                },
                selected = fetchedLocation
            )
        }

        // Trigger location fetch only when user clicks
        if (triggerLocationFetch && !fetchedLocation) {
            LocationScreen { address ->
                if (address.isNotEmpty()) {
                    addressLine.value = address[0].getAddressLine(0)
                    city.value = address[0].locality
                    state.value = address[0].adminArea
                    postalCode.value = address[0].postalCode
                    country.value = address[0].countryName

                    fetchedLocation = true
                } else {
                    Toast.makeText(context, "Failed to fetch location", Toast.LENGTH_SHORT).show()
                }
                triggerLocationFetch = false // reset trigger
            }
        }

        OutlinedButton(
            onClick = {
                val address = UserAddress(
                    isDefault = isSelectedDefault,
                    addressLine = addressLine.value,
                    city = city.value,
                    state = state.value,
                    postalCode = postalCode.value,
                    country = country.value
                )

                profileViewModel.saveUserAddress(address)
                Toast.makeText(context, "Address Saved Successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("profile_screen") {
                    popUpTo("address") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = addressLine.value.isNotEmpty()
                    && city.value.isNotEmpty()
                    && state.value.isNotEmpty()
                    && postalCode.value.isNotEmpty()
                    && country.value.isNotEmpty()
        ) {
            Text(text = "Save")
        }
    }
}
