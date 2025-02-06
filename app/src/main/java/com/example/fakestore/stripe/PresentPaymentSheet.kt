package com.example.fakestore.stripe

import com.stripe.android.paymentsheet.PaymentSheet

 fun presentPaymentSheet(
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

