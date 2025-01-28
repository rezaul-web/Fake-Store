package com.example.fakestore.offer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.fakestore.allProducts.StarRatingBar
import com.example.fakestore.allProducts.AllProductsViewModel
import com.example.fakestore.models.allproducts.ProductItem
import com.example.fakestore.network.Resource
import kotlin.math.roundToInt

@Composable
fun OfferScreen(homeViewModel: AllProductsViewModel = hiltViewModel(), navController: NavController) {
    val allProducts by homeViewModel.allProducts.collectAsState()
    when (val state = allProducts) {
        is Resource.Error -> {
            Text(
                text = state.message ?: "An error occurred.",
                color = Color.Red,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
            Button(onClick = { homeViewModel.getAllProducts() }) {
                Text("Retry")
            }
        }

        Resource.Idle -> {
            Text("Idle State. Start fetching products.")
        }

        Resource.Loading -> {
            CircularProgressIndicator()
        }

        is Resource.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns fixed
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.data.size) { index ->
                    val product = state.data[index]

                    // Generate random discount and create a DiscountedProduct
                    val discountPercent = (20..40).random()
                    val discountedPrice = ((product.price * 85) * (1 - discountPercent / 100f)).roundToInt()
                    val discountedProduct = DiscountedProduct(
                        originalProduct = product,
                        discountedPrice = discountedPrice,
                        discountPercent = discountPercent
                    )

                    ProductCardWithDiscount(
                        discountedProduct = discountedProduct,
                        onProductClick = {
                            homeViewModel.selectDiscountedProduct(discountedProduct)
                            navController.navigate("detail_screen_two")
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ProductCardWithDiscount(discountedProduct: DiscountedProduct, onProductClick: () -> Unit) {
    val product = discountedProduct.originalProduct

    Card(
        onClick = { onProductClick() },
        modifier = Modifier
            .width(180.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Product Image
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Product Title
            Text(
                text = product.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Product Description
            Text(
                text = product.description,
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Product Price and Discount
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "MRP: \u20B9${(product.price * 85).roundToInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Red,
                    textDecoration = TextDecoration.LineThrough
                )
                Text(
                    text = "Offer Price: \u20B9${discountedProduct.discountedPrice} (${discountedProduct.discountPercent}% OFF)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            }

            // Star Rating Bar
            Spacer(modifier = Modifier.height(4.dp))

            StarRatingBar(
                rating = product.rating.rate.toFloat(),
                onRatingChanged = {} // Optional interactivity
            )
        }
    }
}





data class DiscountedProduct(
    val originalProduct: ProductItem,
    val discountedPrice: Int,
    val discountPercent: Int
)
