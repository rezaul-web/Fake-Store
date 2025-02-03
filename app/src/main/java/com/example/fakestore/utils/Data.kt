package com.example.fakestore.utils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import com.example.fakestore.mainapp.NavigationItem

object Data {
    const val PUBLISHABLE_KEY = "pk_test_51QnIS2Qdx9iW1MLcdCsHenOsClYZ1dA4vRUEf" +
            "id2Ec93mRSTfWujJp5I8csB8Bq7tK4hczvXCofnUrkpMJ56Jtiq00XVa3xNdV"
    const val SECRET_KEY = "sk_test_51QnIS2Qdx9iW1MLcJXEbBTvXRZc2Aeguu5h3u3zbJpU" +
            "9pv3cFoxjJZkXHtlQVEti3H8eP2Utji1ReCibIO8dAg7L00trork0jD"
    const val STRIPE_API_VERSION = "2022-11-15"
    const val CURRENCY = "inr"
    const val STRIPE_URL= "https://api.stripe.com/v1/"

 

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