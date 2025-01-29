package com.example.fakestore.ordersmanagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.fakestore.R
import com.example.fakestore.allProducts.AllProductsViewModel
import com.example.fakestore.model.ProductItem
import kotlin.math.roundToInt

@Composable
fun OrderSummaryScreen(
    allProductsViewModel: AllProductsViewModel,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    navController: NavController
) {
    val defaultAddress by ordersViewModel.defaultAddress.collectAsState()

    val selectedProducts by allProductsViewModel.selectedProduct.collectAsState()
    val deliveryCharge by ordersViewModel.deliveryCharge.collectAsState()
    val otherCharges by ordersViewModel.otherCharges.collectAsState()
    val quantity by ordersViewModel.quantity.collectAsState()



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

            Column(androidx.compose.ui.Modifier.padding(6.dp))    {
                    OrderDetailRow("Product Price:", "₹${(product.price * 85).roundToInt()}")
                    OrderDetailRow("Delivery Charges:", "₹$deliveryCharge")
                    OrderDetailRow("Other Charges:", "₹$otherCharges")
                    OrderDetailRow("Quantity:", "$quantity")
                    OrderDetailRow("Total:", "₹${(product.price * 85 * quantity).roundToInt()}", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer Card with payment button
                PaymentCard((product.price *85 *quantity).roundToInt())
            }
        }
    }
}

@Composable
fun OrderDetailRow(label: String, value: String, fontWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, fontWeight = fontWeight)
        )
    }
}

@Composable
fun PaymentCard(totalPrice: Int) {
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
                onClick = { /* Handle payment logic */ },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    "Proceed to Payment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
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
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )

                    // Increase Quantity Button
                    IconButton(
                        onClick = { onIncrease() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Quantity"
                        )
                    }
                    IconButton(
                        onClick = { onDecrease() },
                        enabled = quantity > 1 // Disable the button if quantity is 1
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_minimize_24),
                            contentDescription = "Decrease Quantity",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}
