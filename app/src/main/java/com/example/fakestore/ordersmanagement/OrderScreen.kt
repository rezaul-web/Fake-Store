package com.example.fakestore.ordersmanagement

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderScreen(
    navController: NavController,
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val userId = firebaseAuth.uid
    LocalContext.current

    val pending = "pending"
    val delivered = "delivered"

    val orders = remember { mutableStateListOf<Map<String, Any>>() }
    val pastOrders = remember { mutableStateListOf<Map<String, Any>>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }



    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", pending)
                .get()
                .addOnSuccessListener { result ->
                    orders.clear()
                    for (document in result) {
                        orders.add(document.data)
                    }
                    isLoading = false
                    errorMessage = if (orders.isEmpty()) "No orders found." else null
                }
                .addOnFailureListener {
                    isLoading = false
                    errorMessage = "Failed to fetch orders. Please try again."
                }

            firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", delivered)
                .get()
                .addOnSuccessListener {
                    pastOrders.clear()
                    for (document in it) {
                        pastOrders.add(document.data)
                    }
                }
        } else {
            isLoading = false
            errorMessage = "User not logged in."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp),  // Increased padding for better spacing
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Your Orders",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4392F9)),
            modifier = Modifier.padding(bottom = 8.dp) // Add some bottom padding for spacing
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),  // Spacing for the loading indicator
                    color = Color(0xFF4392F9) // Matching the app's primary color
                )
            }

            errorMessage != null -> {
                Text(
                    "No orders history found",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red),
                    modifier = Modifier.padding(16.dp)  // Padding for better spacing
                )
            }

            orders.isEmpty() -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "No Orders",
                        tint = Color.Gray,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No orders found.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp) // Add some side padding
                    )
                }
            }

            else -> {
                LazyColumn {
                    item {
                        Text(
                            "Pending Orders",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(orders ) { order ->
                        OrderItemCard(order)
                    }
                    item() {
                        Spacer(Modifier.size(8.dp))
                        Text(
                            "Past Orders",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(pastOrders.reversed()) { order ->
                        OrderItemCard(order)
                    }
                }
            }
        }
    }
}


@Composable
fun OrderItemCard(order: Map<String, Any>) {
    val timestamp = order["date"] as? com.google.firebase.Timestamp
    val formattedDate = timestamp?.toDate()?.let { date ->
        SimpleDateFormat("dd-MM-yy hh:mm a", Locale.getDefault()).format(date)
    } ?: "Unknown Date"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = order["imageUrl"] as String,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order["productTitle"] as String,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: ${(order["price"])}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Quantity: ${order["quantity"]}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Delivery: ₹${order["deliveryCharge"]}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Other Charges: ₹${order["otherCharges"]}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Total: ₹${order["total"]}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF388E3C) // Green color for price
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Ordered on: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}