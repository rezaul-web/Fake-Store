package com.example.fakestore.utils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import com.example.fakestore.mainapp.NavigationItem

object BottomBarItems {
 

    val shoppingAppNavigationItems = listOf(
        NavigationItem(
            route = "all_products",
            icon = Icons.Default.Home,
            title = "Home"
        ),
        NavigationItem(
            route = "cart_screen",
            icon = Icons.Default.ShoppingCart,
            title = "Cart"
        ),
        NavigationItem(
            route = "profile_screen",
            icon = Icons.Default.Person,
            title = "Profile"
        ),
        NavigationItem(
            route = "orders_screen",
            icon = Icons.AutoMirrored.Filled.List,
            title = "Orders"
        )
    )

}