package com.example.fakestore.stripe

import com.stripe.android.paymentsheet.PaymentSheetResult

 fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult,onSuccess:()->Unit,onFailure:()->Unit,onCanceled:()->Unit={}) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            onCanceled()
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