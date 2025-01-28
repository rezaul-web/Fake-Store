package com.example.fakestore.offer

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.fakestore.allProducts.StarRatingBar
import com.example.fakestore.cart.CartViewModel
import com.example.fakestore.allProducts.AllProductsViewModel
import kotlin.math.roundToInt

@Composable
fun DetailScreenDiscounted(
    onAddToCart: (DiscountedProduct) -> Unit,
    onBuyNow: (DiscountedProduct) -> Unit,
    sharedViewModel: AllProductsViewModel,
    cartViewmodel: CartViewModel = hiltViewModel()
) {
    val product by sharedViewModel.selectedDisCountedProduct.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Product Details
        Spacer(modifier = Modifier.height(16.dp))
        product?.let {
            Image(
                painter = rememberAsyncImagePainter(model = it.originalProduct.image),
                contentDescription = it.originalProduct.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it.originalProduct.title,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Category: ${it.originalProduct.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Offer Price: \u20B9${it.discountedPrice}",
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "Original Price: \u20B9${(it.originalProduct.price * 85).roundToInt()}",
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it.originalProduct.description,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Rating Section
        product?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingBar(rating = it.originalProduct.rating.rate.toFloat()) { }
                Text(
                    text = "(${it.originalProduct.rating.count} reviews)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
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
                            productId = it.originalProduct.id.toString(),
                            price = it.discountedPrice.toDouble(),
                            name = it.originalProduct.title,
                            quantity = 1,
                            imageUrl = it.originalProduct.image
                        )
                    }
                    Toast.makeText(context, "Item Added to the Cart", Toast.LENGTH_SHORT).show()
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

