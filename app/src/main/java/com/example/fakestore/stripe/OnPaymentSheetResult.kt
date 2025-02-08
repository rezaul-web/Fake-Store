package com.example.fakestore.stripe

import com.stripe.android.paymentsheet.PaymentSheetResult

 fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult,onSuccess:()->Unit,onFailure:()->Unit) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            println("Payment Sheet canceled")
        }

        is PaymentSheetResult.Failed -> {
            println("Payment Sheet failed: ${paymentSheetResult.error}")
            onFailure()
        }

        is PaymentSheetResult.Completed -> {
            println("Payment Sheet completed successfully")
            onSuccess()
        }
    }
}