package com.example.fakestore.allProducts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fakestore.R
import com.example.fakestore.home.HomeViewModel
import com.example.fakestore.network.Resource

@Composable
fun AllProducts(viewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    val allProducts by viewModel.allProducts.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf("All") }

    // The network call should only be triggered once or on some manual refresh, not every time


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.White
            ),
            label = { Text("Search any product..") },
            trailingIcon = {
                IconButton(onClick = {}) {
                    Image(painterResource(R.drawable.mic), contentDescription = null)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )

        when (val state = allProducts) {
            is Resource.Error -> {
                Text(
                    text = state.message ?: "An error occurred.",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
                Button(onClick = { viewModel.getAllProducts() }) {
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
                    contentPadding = PaddingValues(4.dp), // Padding around the grid
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between columns
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Space between rows
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.data.size) { index ->
                        val product = state.data[index]

                        ProductCard(
                            product = product,
                            onProductClick = {
                                viewModel.selectProduct(product) // Only select the product, don't trigger network
                                navController.navigate("detail_screen") // Navigate to detail screen
                            }
                        )
                    }
                }
            }
        }
    }
}
