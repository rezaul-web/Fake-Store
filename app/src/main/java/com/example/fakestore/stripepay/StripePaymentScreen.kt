package com.example.fakestore.stripepay

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stripe.model.Card

@Composable
fun StripePaymentScreen(viewModel: StripeViewModel = hiltViewModel()) {
    val paymentState by viewModel.paymentState.collectAsState()

    when (val state = paymentState) {
        is PaymentState.Idle -> {
            IdleScreen(onStartPayment = { viewModel.createCustomer() })
        }
        is PaymentState.Loading -> {
            LoadingScreen()
        }
        is PaymentState.Success -> {
            PaymentSuccessScreen(clientSecret = state.clientSecret)
        }
        is PaymentState.Error -> {
            Log.e("StripePaymentScreen", "Error: ${state.message}")
            ErrorScreen(errorMessage = state.message, onRetry = { viewModel.createCustomer() })
        }


    }
}

@Composable
fun IdleScreen(onStartPayment: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Start Payment",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStartPayment) {
            Text(text = "Pay Now")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun PaymentSuccessScreen(clientSecret: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Payment Successful!",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Client Secret: $clientSecret",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}