package com.example.fakestore.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.models.allproducts.UserAddress
import com.example.fakestore.utils.FakeStoreTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


const val USER_ADDRESS_SCREEN = "user_address"

@Composable
fun UserAddressScreen(navController: NavController,
                      firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
                      firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
                      profileViewModel: ProfileViewModel= hiltViewModel()
                      ) {
    val context= LocalContext.current

    val addressLine = remember {
        mutableStateOf( "")
    }
    val city = remember {
        mutableStateOf( "")
    }
    val state = remember {
        mutableStateOf( "")
    }
    val postalCode = remember {
        mutableStateOf( "")
    }
    val country = remember {
        mutableStateOf( "")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
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

        OutlinedButton (
            onClick = {
                val address = UserAddress(
                    addressLine = addressLine.value,
                    city = city.value,
                    state = state.value,
                    postalCode = postalCode.value,
                    country = country.value
                )

            profileViewModel.saveUserAddress(address)
                Toast.makeText(context,"Address Saved Successfully",Toast.LENGTH_SHORT).show()
                navController.navigate("profile_screen")
            }, modifier = Modifier.fillMaxWidth(),
            enabled = addressLine.value.isNotEmpty() && city.value.isNotEmpty() && state.value.isNotEmpty() && postalCode.value.isNotEmpty() && country.value.isNotEmpty()
        ) {
            Text(text = "Save")
        }
    }
}

