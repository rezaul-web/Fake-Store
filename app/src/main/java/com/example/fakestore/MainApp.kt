package com.example.fakestore

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.allProducts.AllProducts
import com.example.fakestore.allProducts.DetailScreen
import com.example.fakestore.allProducts.utils.BottomBarItems
import com.example.fakestore.auth.AuthScreen
import com.example.fakestore.auth.login.LogInScreen
import com.example.fakestore.auth.signup.SignUpScreen
import com.example.fakestore.home.BottomBar
import com.example.fakestore.home.HomeScreen
import com.example.fakestore.home.HomeViewModel
import com.example.fakestore.order.CartScreen
import com.example.fakestore.order.OrderScreen
import com.example.fakestore.order.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
@Composable
fun MainApp(
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val bottomBarHiddenRoutes = listOf("auth_screen", "log_in", "sign_up", )

    val user = auth.currentUser
    val startDestination = if (user == null) "auth_screen" else "all_products"


    val navController = rememberNavController()
    // Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute !in bottomBarHiddenRoutes) {
                BottomBar(
                    icons = BottomBarItems.shoppingAppNavigationItems,
                    navController = navController
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "all_products") {
                AllProducts(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(route = "auth_screen") {
                AuthScreen(navController = navController)
            }
            composable(route = "home_screen") { HomeScreen() }
            composable(route = "log_in") {

                LogInScreen(navController = navController)

            }
            composable(route = "sign_up") { SignUpScreen(navController = navController) }
            composable(route = "detail_screen") {
                DetailScreen(
                    onBuyNow = { /* Handle Buy Now */ },
                    onAddToCart = { /* Handle Add to Cart */ },
                    sharedViewModel = viewModel
                )
            }
            composable(route = "cart_screen") { CartScreen() }
            composable(route = "profile_screen") { ProfileScreen(navController = navController) }
            composable(route = "orders_screen") { OrderScreen() }
        }
    }
}
