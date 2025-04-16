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
import androidx.compose.material.icons.filled.Clear

import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
            isLoading = true
            errorMessage = null

            // Fetch Pending Orders
            firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", pending)
                .get()
                .addOnSuccessListener { result ->
                    orders.clear()
                    for (document in result) {
                        orders.add(document.data)
                    }
                }
                .addOnFailureListener {
                    errorMessage = "Failed to fetch pending orders. Please try again."
                }

            // Fetch Past Orders
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
                .addOnFailureListener {
                    if (errorMessage == null) {
                        errorMessage = "Failed to fetch past orders. Please try again."
                    }
                }
                .addOnCompleteListener {
                    isLoading = false
                    if (orders.isEmpty() && pastOrders.isEmpty() && errorMessage == null) {
                        errorMessage = "No orders found."
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Your Orders",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4392F9)
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        when {
            isLoading -> {
                CircularProgressIndicator(color = Color(0xFF4392F9))
            }

            errorMessage != null -> {
                Text(
                    errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }

            orders.isEmpty() && pastOrders.isEmpty() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize().padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "No Orders",
                        tint = Color.Gray,
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        "No orders found yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (orders.isNotEmpty()) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Pending Orders", tint = Color(0xFFFFA000))
                                Text(
                                    "Pending Orders",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.Black,
                                )
                            }
                            Divider()
                        }
                        items(orders) { order ->
                            OrderItemCard(order)
                        }
                    }

                    if (pastOrders.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Past Orders", tint = Color.Gray)
                                Text(
                                    "Past Orders",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.Black
                                )
                            }
                            Divider()
                        }
                        items(pastOrders.reversed()) { order ->
                            OrderItemCard(order)
                        }
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
        SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(date)
    } ?: "Unknown Date"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)) // Subtle background
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = order["imageUrl"] as String,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        text = order["productTitle"] as String,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Quantity: ${order["quantity"]}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Ordered on: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Price:", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text("₹${order["price"]}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delivery:", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text("₹${order["deliveryCharge"]}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Other Charges:", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text("₹${order["otherCharges"]}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(
                    "₹${order["total"]}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF388E3C) // Green color for total price
                )
            }
        }
    }
}