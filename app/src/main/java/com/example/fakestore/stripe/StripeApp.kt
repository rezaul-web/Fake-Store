package com.example.fakestore.stripe

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun StripeApp(stripeViewModel: StripeViewModel= hiltViewModel()) {
    // Payment Sheet state
    val paymentSheet = rememberPaymentSheet(::onPaymentSheetResult)
    val result by stripeViewModel.stripeResponse.collectAsState()
    // Context for PaymentConfiguration
    val context = LocalContext.current
    // State for Payment Sheet configuration
    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }

    // Fetch Payment Sheet details from the backend
    LaunchedEffect(result) {
        try {

            Log.d("result",result.toString())

            // Update state with fetched details
            paymentIntentClientSecret = result?.paymentIntent
            customerConfig = result?.let {
                PaymentSheet.CustomerConfiguration(
                    id = it.customer,
                    ephemeralKeySecret = result!!.ephemeralKey
                )
            }

            // Initialize Stripe with the publishable key
            result?.let { PaymentConfiguration.init(context, it.publishableKey) }
        } catch (e: Exception) {
            // Handle errors (e.g., show a snackbar or log the error)
            println("Error fetching Payment Sheet details: ${e.message}")
        }
    }

    // Button to launch the Payment Sheet
    Button(
        onClick = {
            val currentConfig = customerConfig
            val currentClientSecret = paymentIntentClientSecret

            if (currentConfig != null && currentClientSecret != null) {
                presentPaymentSheet(paymentSheet, currentConfig, currentClientSecret)
            } else {
                println("Payment Sheet configuration is not ready.")
            }
        }
    ) {
        Text("Checkout")
    }
}

// Helper function to present the Payment Sheet
private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    paymentIntentClientSecret: String
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "My Merchant Name",
            customer = customerConfig,
            allowsDelayedPaymentMethods = true // Enable if you support delayed payment methods
        )
    )
}

// Handle Payment Sheet results
 fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult,onFailure:()->Unit={},onSuccess:()->Unit={},) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            println("Payment Sheet canceled")

        }

        is PaymentSheetResult.Failed -> {
            onFailure()
            println("Payment Sheet failed: ${paymentSheetResult.error}")
        }

        is PaymentSheetResult.Completed -> {
            onSuccess()
            println("Payment Sheet completed successfully")
            // Handle successful payment (e.g., navigate to a confirmation screen)
        }
    }
}