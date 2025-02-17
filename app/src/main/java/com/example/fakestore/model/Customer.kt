package com.example.fakestore.model

data class Customer(
    val address: Any,
    val balance: Int,
    val created: Int,
    val currency: Any,
    val default_source: Any,
    val delinquent: Boolean,
    val description: Any,
    val discount: Any,
    val email: Any,
    val id: String,
    val invoice_prefix: String,
    val invoice_settings: InvoiceSettings,
    val livemode: Boolean,
    val metadata: Metadata,
    val name: Any,
    val next_invoice_sequence: Int,
    val `object`: String,
    val phone: Any,
    val preferred_locales: List<Any>,
    val shipping: Any,
    val tax_exempt: String,
    val test_clock: Any
)