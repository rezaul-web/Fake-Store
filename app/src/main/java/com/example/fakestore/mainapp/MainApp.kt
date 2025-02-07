package com.example.fakestore.mainapp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fakestore.allProducts.AllProducts
import com.example.fakestore.allProducts.AllProductsViewModel
import com.example.fakestore.allProducts.DetailScreen
import com.example.fakestore.auth.AuthScreen
import com.example.fakestore.auth.login.LogInScreen
import com.example.fakestore.auth.signup.SignUpScreen
import com.example.fakestore.cart.BuyFromCart
import com.example.fakestore.cart.CartScreen
import com.example.fakestore.home.HomeScreen
import com.example.fakestore.offer.OfferScreen
import com.example.fakestore.ordersmanagement.OrderConfirmationScreen
import com.example.fakestore.ordersmanagement.OrderScreen
import com.example.fakestore.ordersmanagement.OrderSummaryScreen
import com.example.fakestore.ordersmanagement.PaymentScreen
import com.example.fakestore.profile.ProfileScreen
import com.example.fakestore.profile.UpdateAddressScreen
import com.example.fakestore.profile.UserAddressScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp(
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    viewModel: AllProductsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val user = auth.currentUser
    val startDestination = if (user == null) Route.AuthScreen.route else Route.AllProducts.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {
        composable(Route.AllProducts.route) {
            AllProducts(viewModel = viewModel, navController = navController)
        }
        composable(Route.AuthScreen.route) {
            AuthScreen(navController = navController)
        }
        composable(Route.HomeScreen.route) {
            HomeScreen()
        }
        composable(Route.LogIn.route) {
            LogInScreen(navController = navController)
        }
        composable(Route.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        composable(Route.DetailScreen.route) {
            DetailScreen(sharedViewModel = viewModel, navController = navController)
        }
        composable(Route.CartScreen.route) {
            CartScreen(navController = navController)
        }
        composable(Route.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
        composable(Route.UserAddress.route) {
            UserAddressScreen(navController = navController)
        }
        composable(Route.OrdersScreen.route) {
            OrderScreen(navController = navController)
        }
        composable(Route.OfferScreen.route) {
            OfferScreen(navController = navController)
        }
        composable(Route.SummaryScreen.route) {
            OrderSummaryScreen(allProductsViewModel = viewModel, navController = navController)
        }
        composable(Route.PaymentScreen.route) {
            PaymentScreen()
        }
        composable("${Route.OrderConfirmation.route}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderConfirmationScreen(orderId = orderId, navController = navController)
        }
        composable(Route.BuyFromCart.route) {
            BuyFromCart(navController = navController)
        }
        composable(
            "updateAddress/{addressLine}/{city}/{state}/{postalCode}/{country}/{isDefault}",
            arguments = listOf(
                navArgument("isDefault") { type = NavType.StringType },
                navArgument("addressLine") { type = NavType.StringType },
                navArgument("city") { type = NavType.StringType },
                navArgument("state") { type = NavType.StringType },
                navArgument("postalCode") { type = NavType.StringType },
                navArgument("country") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isDefault = backStackEntry.arguments?.getString("isDefault") == "true"
            val addressLine = backStackEntry.arguments?.getString("addressLine") ?: ""
            val city = backStackEntry.arguments?.getString("city") ?: ""
            val state = backStackEntry.arguments?.getString("state") ?: ""
            val postalCode = backStackEntry.arguments?.getString("postalCode") ?: ""
            val country = backStackEntry.arguments?.getString("country") ?: ""

            UpdateAddressScreen(
                isDefault = isDefault,
                navController = navController,
                addressLine = addressLine,
                city = city,
                state = state,
                postalCode = postalCode,
                country = country
            )
        }
    }
}

