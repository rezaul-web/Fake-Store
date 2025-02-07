package com.example.fakestore.mainapp

sealed class Route(val route: String) {
    data object AllProducts : Route("all_products")
   data object AuthScreen : Route("auth_screen")
   data object HomeScreen : Route("home_screen")
   data object LogIn : Route("log_in")
   data object SignUp : Route("sign_up")
   data object DetailScreen : Route("detail_screen")
   data object CartScreen : Route("cart_screen")
   data object ProfileScreen : Route("profile_screen")
   data object UserAddress : Route("address")
   data object OrdersScreen : Route("orders_screen")
   data object OfferScreen : Route("offer_screen")
   data object SummaryScreen : Route("summery_screen")
  data  object PaymentScreen : Route("payment_screen")
   data object BuyFromCart : Route("buy_from_cart")
    data object OrderConfirmation:Route("order_confirmed")

    data class UpdateAddress(
        val addressLine: String,
        val city: String,
        val state: String,
        val postalCode: String,
        val country: String,
        val isDefault: Boolean
    ) : Route("updateAddress/$addressLine/$city/$state/$postalCode/$country/$isDefault")
}