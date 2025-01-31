package com.example.fakestore

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fakestore.allProducts.AllProducts
import com.example.fakestore.allProducts.DetailScreen
import com.example.fakestore.utils.BottomBarItems
import com.example.fakestore.auth.AuthScreen
import com.example.fakestore.auth.login.LogInScreen
import com.example.fakestore.auth.signup.SignUpScreen
import com.example.fakestore.home.FakeStoreBottomBar
import com.example.fakestore.home.HomeScreen
import com.example.fakestore.allProducts.AllProductsViewModel
import com.example.fakestore.cart.BuyFromCart
import com.example.fakestore.cart.CartScreen
import com.example.fakestore.offer.DetailScreenDiscounted
import com.example.fakestore.offer.OfferScreen
import com.example.fakestore.ordersmanagement.OrderScreen
import com.example.fakestore.ordersmanagement.OrderSummaryScreen
import com.example.fakestore.ordersmanagement.PaymentScreen
import com.example.fakestore.profile.ProfileScreen
import com.example.fakestore.profile.UpdateAddressScreen
import com.example.fakestore.profile.UserAddressScreen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    viewModel: AllProductsViewModel = hiltViewModel()
) {
    val bottomBarHiddenRoutes = listOf("auth_screen", "log_in", "sign_up","detail_screen","summery_screen","payment_screen","buy_from_cart")

    val user = auth.currentUser
    val startDestination = if (user == null) "auth_screen" else "all_products"
    val scrollBehavior= TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val navController = rememberNavController()
    // Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute !in bottomBarHiddenRoutes) {
                FakeStoreBottomBar(
                    icons = BottomBarItems.shoppingAppNavigationItems,
                    navController = navController
                )
            }
        },
        topBar = {
           if (currentRoute !in bottomBarHiddenRoutes) { FakeStoreTopAppBar(scrollBehavior = scrollBehavior) }

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
                    sharedViewModel = viewModel,
                    navController = navController
                )
            }
            composable(route = "detail_screen_two") {
                DetailScreenDiscounted(
                    onBuyNow = { /* Handle Buy Now */ },
                    onAddToCart = { /* Handle Add to Cart */ },
                    sharedViewModel = viewModel
                )
            }
            composable(route = "cart_screen") { CartScreen(navController = navController) }
            composable(route = "profile_screen") { ProfileScreen(navController = navController) }
            composable(route = "address") { UserAddressScreen(navController = navController) }
            composable(route = "orders_screen") { OrderScreen() }
            composable(route = "offer_screen") { OfferScreen(navController = navController) }


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

                val isDefault = backStackEntry.arguments?.getString("isDefault") ?: ""
                val addressLine = backStackEntry.arguments?.getString("addressLine") ?: ""
                val city = backStackEntry.arguments?.getString("city") ?: ""
                val state = backStackEntry.arguments?.getString("state") ?: ""
                val postalCode = backStackEntry.arguments?.getString("postalCode") ?: ""
                val country = backStackEntry.arguments?.getString("country") ?: ""
                UpdateAddressScreen(
                    isDefault = isDefault=="true",
                    navController = navController,
                    addressLine = addressLine,
                    city = city,
                    state = state,
                    postalCode = postalCode,
                    country = country
                )
            }
            composable(route = "summery_screen"
            ) {

                OrderSummaryScreen(allProductsViewModel = viewModel,navController=navController)
            }

            composable(route="payment_screen") {
                PaymentScreen()
            }
            composable(route="buy_from_cart") {
                BuyFromCart(navController = navController)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakeStoreTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(24.dp) // Adjust size as needed
                )
                Spacer(modifier = Modifier.width(8.dp)) // Add spacing between icon and text
                Text(text = "Stylish ", color = Color(0xFF4392F9))
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

