package com.example.fakestore.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fakestore.mainapp.Route
import com.example.fakestore.ordersmanagement.AddressCard
import com.example.fakestore.ordersmanagement.OrderDetailRow
import com.example.fakestore.ordersmanagement.OrdersViewModel
import com.example.fakestore.ordersmanagement.PaymentCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun BuyFromCart(

    ordersViewModel: OrdersViewModel = hiltViewModel(),
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel(),
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val defaultAddress = ordersViewModel.defaultAddress.collectAsState()
    val deliveryCharge by ordersViewModel.deliveryCharge.collectAsState()
    val otherCharges by ordersViewModel.otherCharges.collectAsState()
    val quantity by ordersViewModel.quantity.collectAsState()
    val cartItems = cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val totalQuantity by cartViewModel.totalQuantity.collectAsState()
    val userId = firebaseAuth.currentUser?.uid
    val context = LocalContext.current
    if (userId == null) {
        Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
        return
    }

    val ordersRef = firestore.collection("orders")
    val orders = cartItems.value.map { product ->
        mapOf(
            "price" to (product.price * 85).roundToInt(),
            "userId" to userId,
            "quantity" to product.quantity,
            "deliveryCharge" to deliveryCharge,
            "total" to (product.price * 85 * quantity).roundToInt() + deliveryCharge + otherCharges,
            "otherCharges" to otherCharges,
            "productId" to product.productId,
            "productTitle" to product.name,
            "imageUrl" to product.imageUrl,
            "date" to Date(),
            "status" to "pending",
            "address" to defaultAddress.value
        )

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Order Summary",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column {
           cartItems.value.forEach { item ->

                CartItemView(item, onDelete = {

                },
                    onDecrement = {
                        cartViewModel.updateCartItemQuantity(item.productId, false)
                    },
                    onIncrement = {
                        cartViewModel.updateCartItemQuantity(item.productId, true)
                    }
                )  // Composable for displaying individual cart item
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Column(Modifier.padding(6.dp)) {
            OrderDetailRow("Delivery Charges:", "₹$deliveryCharge")
            OrderDetailRow("Other Charges:", "₹$otherCharges")
            OrderDetailRow("Quantity:", "$totalQuantity")
            OrderDetailRow(
                "Total:",
                "₹${(totalPrice * 85 * quantity) + deliveryCharge + otherCharges}",
                fontWeight = FontWeight.Bold
            )
        }
        defaultAddress.value?.let {
            AddressCard(address = it) {
                navController.navigate(Route.ProfileScreen.route)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer Card with payment button
        PaymentCard((totalPrice * 85 * quantity) + deliveryCharge + otherCharges) {
            orders.forEach { order ->
                ordersRef.add(order)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Orders Placed", Toast.LENGTH_SHORT).show()

                        cartViewModel.deleteFromCart(order["productId"].toString())
                        navController.navigate(Route.AllProducts.route)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed, Please Try Again", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }
}







