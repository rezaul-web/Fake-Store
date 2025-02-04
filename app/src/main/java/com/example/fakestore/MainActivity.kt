package com.example.fakestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.mainapp.FakeStoreBottomBar
import com.example.fakestore.mainapp.FakeStoreTopAppBar
import com.example.fakestore.mainapp.MainApp
import com.example.fakestore.mainapp.Route
import com.example.fakestore.ui.theme.FakeStoreTheme
import com.example.fakestore.utils.Data
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val bottomBarHiddenRoutes = listOf(
                Route.AuthScreen.route, Route.LogIn.route, Route.SignUp.route,
                Route.SummaryScreen.route, Route.PaymentScreen.route,
                Route.BuyFromCart.route,
            )
            val user= FirebaseAuth.getInstance().uid
            val navController= rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            FakeStoreTheme {
                Scaffold(
                    bottomBar = {
                        if (currentRoute !in bottomBarHiddenRoutes) {
                            FakeStoreBottomBar(
                                icons = Data.shoppingAppNavigationItems,
                                navController = navController
                            )
                        }
                    },
                    topBar = {
                        if (currentRoute !in bottomBarHiddenRoutes) {
                            FakeStoreTopAppBar(navController = navController)
                        }
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding) // Apply innerPadding here
                    ) {
                        MainApp(navController = navController)
                    }
                }
            }
        }
    }
}