package com.example.fakestore.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
    val currentUserUid = firebaseAuth.currentUser?.uid

    // State to hold the user profile data
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch user data from Firestore
    LaunchedEffect(currentUserUid) {
        if (currentUserUid != null) {
            try {
                val snapshot = firestore.collection("users")
                    .document(currentUserUid)
                    .get()
                    .await()
                if (snapshot.exists()) {
                    userData = snapshot.data
                } else {
                    errorMessage = "User profile not found."
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userData != null) {
            Text("Profile Screen", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Full Name: ${userData?.get("fullName") ?: "N/A"}")
            Text("Email: ${userData?.get("email") ?: "N/A"}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                showDialog = true
            }) {
                Text("Sign Out")
            }
        } else if (errorMessage != null) {
            Text(errorMessage ?: "An unknown error occurred.", color = Color.Red)
        } else {
            CircularProgressIndicator()
        }
    }
    if (showDialog) {
        FakeStoreAlertDialog(
            onConfirmation = {
                firebaseAuth.signOut()
                navController.navigate("log_in")
                showDialog=false
            },
            onDismissRequest = {
                showDialog=false
            },

            dialogTitle = "Sign Out",
            dialogText = "You Are about to Sign Out"
        )
    }
}
