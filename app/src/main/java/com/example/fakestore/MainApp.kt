package com.example.fakestore

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.allProducts.AllProducts
import com.example.fakestore.allProducts.DetailScreen
import com.example.fakestore.auth.AuthScreen
import com.example.fakestore.auth.login.LogInScreen
import com.example.fakestore.auth.signup.SignUpScreen
import com.example.fakestore.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    viewModel: HomeViewModel = hiltViewModel() // Inject ViewModel){}
) {
    // Check if the user is logged in
    val user = auth.currentUser
    val startDestination = if (user == null) "auth_screen" else "all_products"
    val navController = rememberNavController()

    // Define the navigation graph
    NavHost(navController = navController, startDestination = startDestination) {

        // All Products Screen
        composable(route = "all_products") {
            AllProducts(
                viewModel = viewModel, // Pass the ViewModel here
                navController = navController
            )
        }

        // Auth Screens (Log in and Sign up)
        composable(route = "auth_screen") {
            AuthScreen(navController = navController)
        }

        composable(route = "log_in") {
            LogInScreen(navController = navController)
        }

        composable(route = "sign_up") {
            SignUpScreen(navController = navController)
        }

        // Detail Screen
        composable(route = "detail_screen") {
            // Pass the necessary data to the DetailScreen (onAddToCart, onBuyNow, sharedViewModel)
            DetailScreen(
                onBuyNow = {
                    // Handle Buy Now action
                },
                onAddToCart = {
                    // Handle Add to Cart action
                },
                sharedViewModel = viewModel // Pass the ViewModel to sharedViewModel
            )
        }
    }
}
