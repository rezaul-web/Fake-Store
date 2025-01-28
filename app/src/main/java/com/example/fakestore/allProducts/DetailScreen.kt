package com.example.fakestore.allProducts

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.fakestore.cart.CartViewModel
import com.example.fakestore.models.allproducts.ProductItem
import kotlin.math.roundToInt

@Composable
fun DetailScreen(
    onAddToCart: (ProductItem) -> Unit,
    onBuyNow: (ProductItem) -> Unit,
    sharedViewModel: AllProductsViewModel,
    cartViewmodel: CartViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val product by sharedViewModel.selectedProduct.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Product Details
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = rememberAsyncImagePainter(model = product?.image),
            contentDescription = product?.title,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        product?.let {
            Text(
                text = it.title,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Text(
            text = "Category: ${product?.category}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\u20B9${((product?.price ?: 1.9) * 85).roundToInt()}",
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        product?.let {
            Text(
                text = it.description,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Rating Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            product?.rating?.rate?.let {
                StarRatingBar(rating = it.toFloat()) { }
            }
            Text(
                text = "(${product?.rating?.count} reviews)",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    product?.let {
                        cartViewmodel.addToCart(
                            productId = it.id.toString(),
                            price = it.price,
                            name = it.title,
                            quantity = 1,
                            imageUrl = it.image
                        )
                    }
                    Toast.makeText(context, "Item Added to the Card", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Add to Cart")
            }
            Button(
                onClick = { product?.let { onBuyNow(it) } },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Buy Now")
            }
        }
    }
}
