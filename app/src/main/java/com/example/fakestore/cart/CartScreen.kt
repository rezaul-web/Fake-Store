package com.example.fakestore.cart

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.fakestore.R
import com.example.fakestore.utils.FakeStoreAlertDialog
import kotlin.math.roundToInt

@Composable
fun CartScreen(cartViewModel: CartViewModel = hiltViewModel(),
               navController: NavController
               ) {
    val cartItems = cartViewModel.cartItems.collectAsState()
    val totalPrice = cartItems.value.sumOf { it.price * it.quantity }
    var showDialog by remember { mutableStateOf(false) }

    var currentItem by remember { mutableStateOf<CartItem?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),  // Add padding for outer spacing
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title of the Cart
        Text(text = "Your Cart", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn for scrolling through cart items
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(cartItems.value.size) { index ->
                val item = cartItems.value[index]
                CartItemView(item, onDelete = {
                    currentItem=item
                    showDialog=true
                },
                    onDecrement = {
                        cartViewModel.updateCartItemQuantity(item.productId,false)
                    },
                    onIncrement = {
                        cartViewModel.updateCartItemQuantity(item.productId,true)
                    }
                    )  // Composable for displaying individual cart item
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Total : \u20B9${((totalPrice ?: 1.9) * 85).roundToInt()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Checkout Button
                Button(
                    onClick = {
                        navController.navigate("buy_from_cart")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Proceed to Checkout")
                }
            }
        }
        if (showDialog) {
            FakeStoreAlertDialog(
                onConfirmation = {
                    currentItem?.let {
                        cartViewModel.deleteFromCart(
                            it
                        )
                    }
                    showDialog=false
                },
                onDismissRequest = {
                    showDialog=false
                },

                dialogTitle = "Delete",
                dialogText = "You Are about to remove the Item from the cart"
            )
        }




    }
}

@Composable
fun CartItemView(
    cartItem: CartItem,
    onDelete: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = cartItem.imageUrl,
                contentDescription = cartItem.name,
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
            )

            // Product Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = cartItem.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "Price: \u20B9${((cartItem.price * 85).roundToInt())}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Quantity:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "${cartItem.quantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                }
            }

            // Delete Button
         Column    {
             IconButton(onClick = { onIncrement() }) {
                 Icon(
                     imageVector = Icons.Default.Add,
                     contentDescription = "Increase Quantity",
                     tint = MaterialTheme.colorScheme.primary
                 )
             }
             IconButton(onClick = { onDecrement() }) {
                 Icon(
                     painter = painterResource(R.drawable.baseline_minimize_24),
                     contentDescription = "Decrease Quantity",
                     tint = MaterialTheme.colorScheme.primary
                 )
             }
                IconButton(
                    onClick = { onDelete() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


