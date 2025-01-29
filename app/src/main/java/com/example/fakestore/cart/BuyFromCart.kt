package com.example.fakestore.cart

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fakestore.ordersmanagement.OrderDetailRow
import com.example.fakestore.ordersmanagement.OrdersViewModel
import com.example.fakestore.ordersmanagement.PaymentCard

@Composable
fun BuyFromCart(

    ordersViewModel: OrdersViewModel = hiltViewModel(),
    navController: NavController,
    cartViewModel: CartViewModel= hiltViewModel()
) {


    val deliveryCharge by ordersViewModel.deliveryCharge.collectAsState()
    val otherCharges by ordersViewModel.otherCharges.collectAsState()
    val quantity by ordersViewModel.quantity.collectAsState()
    val cartItems = cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val totalQuantity by cartViewModel.totalQuantity.collectAsState()


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Order Summary",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

           LazyColumn {
               items(cartItems.value.size) { index ->
                   val item = cartItems.value[index]
                   CartItemView(item, onDelete = {

                   },
                       onDecrement = {
                           cartViewModel.updateCartItemQuantity(item.productId,false)
                       },
                       onIncrement = {
                           cartViewModel.updateCartItemQuantity(item.productId,true)
                       }
                   )  // Composable for displaying individual cart item
               }
           }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

            Column(androidx.compose.ui.Modifier.padding(6.dp))    {
                    OrderDetailRow("Delivery Charges:", "₹$deliveryCharge")
                    OrderDetailRow("Other Charges:", "₹$otherCharges")
                    OrderDetailRow("Quantity:", "$totalQuantity")
                    OrderDetailRow("Total:", "₹${(totalPrice * 85 * quantity)+deliveryCharge+otherCharges}", fontWeight = FontWeight.Bold)
                }
               
                Spacer(modifier = Modifier.weight(1f))

                // Footer Card with payment button
                PaymentCard((totalPrice *85 *quantity))
            }
        }
    }






