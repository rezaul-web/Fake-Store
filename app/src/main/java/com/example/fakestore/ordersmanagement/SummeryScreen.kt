package com.example.fakestore.ordersmanagement

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.fakestore.R
import com.example.fakestore.allProducts.AllProductsViewModel
import com.example.fakestore.mainapp.Route
import com.example.fakestore.model.ProductItem
import com.example.fakestore.stripe.StripeViewModel
import com.example.fakestore.stripe.onPaymentSheetResult
import com.example.fakestore.stripe.presentPaymentSheet
import com.example.fakestore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun OrderSummaryScreen(
    allProductsViewModel: AllProductsViewModel,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    navController: NavController,
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    stripeViewModel: StripeViewModel = hiltViewModel()
) {
    val defaultAddress by ordersViewModel.defaultAddress.collectAsState()
    val selectedProducts by allProductsViewModel.selectedProduct.collectAsState()
    val deliveryCharge by ordersViewModel.deliveryCharge.collectAsState()
    val otherCharges by ordersViewModel.otherCharges.collectAsState()
    val quantity by ordersViewModel.quantity.collectAsState()
    val context = LocalContext.current
    val result by stripeViewModel.stripeResponse.collectAsState()
    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }
    val userId = firebaseAuth.currentUser?.uid
    if (userId == null) {
        Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
        return
    }

    var startPayment by remember { mutableStateOf(false) } // Moved to OrderSummaryScreen

    val order = selectedProducts?.let { product ->
        mapOf(
            "price" to (product.price * 85).roundToInt(),
            "userId" to userId,
            "quantity" to quantity,
            "deliveryCharge" to deliveryCharge,
            "total" to (product.price * 85 * quantity).roundToInt() + deliveryCharge + otherCharges,
            "otherCharges" to otherCharges,
            "productId" to product.id,
            "productTitle" to product.title,
            "imageUrl" to product.image,
            "date" to Date(),
            "status" to "pending",
            "address" to defaultAddress
        )
    }

    val ordersRef = firestore.collection("orders")

    val paymentSheet = rememberPaymentSheet { result ->
        onPaymentSheetResult(
            paymentSheetResult = result,
            onSuccess = {
                startPayment = false
                customerConfig = null // Reset customer config
                paymentIntentClientSecret = null // Reset client secret
                if (order != null) {
                    ordersRef.add(order)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
                            val orderId = it.id
                            navController.navigate("${Route.OrderConfirmation.route}/$orderId") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed, Please Try Again", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            onFailure = {
                startPayment = false
                customerConfig = null // Reset customer config
                paymentIntentClientSecret = null // Reset client secret
                Toast.makeText(context, "Payment Failed, Please Try Again", Toast.LENGTH_SHORT).show()
            },
            onCanceled = {
                startPayment = false
                customerConfig = null // Reset customer config
                paymentIntentClientSecret = null // Reset client secret
            }
        )
    }



    when (val state = result) {
        is Resource.Error -> {
            LaunchedEffect(state.message) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            startPayment = false // Stop loading if error
        }
        Resource.Loading -> {
            CircularProgressIndicator()
            startPayment = true // Start loading when fetching payment details
        }
        is Resource.Success -> {
            LaunchedEffect(state.data) {
                Log.d("result", state.data.toString())
                paymentIntentClientSecret = state.data.paymentIntent
                customerConfig = PaymentSheet.CustomerConfiguration(
                    id = state.data.customer,
                    ephemeralKeySecret = state.data.ephemeralKey
                )
                PaymentConfiguration.init(context, state.data.publishableKey)
                if (customerConfig != null && paymentIntentClientSecret != null) {
                    presentPaymentSheet(paymentSheet, customerConfig!!, paymentIntentClientSecret!!)
                }
            }
        }
        Resource.Idle -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        selectedProducts?.let { product ->
            SummeryItemCard(
                product,
                quantity = quantity,
                onIncrease = { ordersViewModel.updateQuantity(true) },
                onDecrease = { ordersViewModel.updateQuantity(false) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.padding(6.dp)) {
                OrderDetailRow("Product Price:", "₹${(product.price * 85).roundToInt()}")
                OrderDetailRow("Delivery Charges:", "₹$deliveryCharge")
                OrderDetailRow("Other Charges:", "₹$otherCharges")
                OrderDetailRow("Quantity:", "$quantity")
                OrderDetailRow(
                    "Total:",
                    "₹${(product.price * 85 * quantity).roundToInt() + deliveryCharge + otherCharges}",
                    fontWeight = FontWeight.Bold
                )
            }

            defaultAddress?.let {
                AddressCard(address = it) {
                    navController.navigate(Route.ProfileScreen.route)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            val totalPrice = (product.price * 85 * quantity + deliveryCharge + otherCharges).roundToInt()

            PaymentCard(totalPrice, startPayment) {
                if (!startPayment) { // Only proceed if payment is not already in progress
                    startPayment = true // Start loading when clicking
                    stripeViewModel.getUser(totalPrice.toString())

                    val currentConfig = customerConfig
                    val currentClientSecret = paymentIntentClientSecret

                    if (currentConfig != null && currentClientSecret != null) {
                        presentPaymentSheet(paymentSheet, currentConfig, currentClientSecret)
                    } else {
                        startPayment = false // Stop loading if payment is not ready
                        println("Payment Sheet configuration is not ready.")
                    }
                }
            }
        } ?: run {
            Text("No product selected", style = MaterialTheme.typography.bodyLarge)
        }
    }
}



// Handle Payment Sheet results


@Composable
fun PaymentCard(totalPrice: Int, startPayment: Boolean, putOrder: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(6.dp)
            ) {
                Text(
                    text = "₹$totalPrice",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { /* Handle view details logic */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("View Details")
                }
            }

            Button(
                onClick = putOrder,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                if (!startPayment) {
                    Text("Proceed to Payment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.White)
                }
            }
        }
    }
}


    @Composable
    fun SummeryItemCard(
        product: ProductItem,
        quantity: Int,
        onIncrease: () -> Unit,
        onDecrease: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .size(80.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)) // Rounded corners for the image
                )

                // Product Details Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp)
                    )
                    Text(
                        text = "Price: ₹${((product.price * 85).roundToInt())}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )

                    // Quantity Row with + and - buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decrease Quantity Button


                        // Quantity Text
                        Text(
                            text = "Quantity: $quantity",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                        )

                        // Increase Quantity Button
                        IconButton(
                            onClick = { onIncrease() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.plus_circle_svgrepo_com),
                                contentDescription = "Increase Quantity",
                                Modifier.size(30.dp)
                            )
                        }
                        IconButton(
                            onClick = { onDecrease() },
                            enabled = quantity > 1 // Disable the button if quantity is 1
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.minus_svgrepo_com),
                                contentDescription = "Decrease Quantity",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }

