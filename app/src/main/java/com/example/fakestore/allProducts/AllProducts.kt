package com.example.fakestore.allProducts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.fakestore.network.Resource


@Composable
fun AllProducts(viewModel: AllProductsViewModel = hiltViewModel(), navController: NavController) {
    val allProducts by viewModel.allProducts.collectAsState()
    val allCategory by viewModel.allCategories.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(4.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = {
                    viewModel.searchedProduct(searchQuery.text.toInt())
                }) {
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
      when(val state =allCategory) {
          Resource.Loading -> {
              CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

          }
          is Resource.Success -> {
              Categories(state.data) {
                  viewModel.getProductsByCategory(it)
              }
          }
          else-> {

          }
      }

        // Offer Image
        Box(
            modifier = Modifier
                .height(189.dp)
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable {
                    navController.navigate("offer_screen")
                }
        ) {
            Image(
                painter = painterResource(R.drawable.offer), contentDescription = null,
                modifier = Modifier.matchParentSize()
            )
        }

        // Products
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
                // Display products in rows of 2
                state.data.chunked(2).forEach { rowProducts ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                        ) {
                        rowProducts.forEach { product ->
                            ProductCard(product = product) {
                                viewModel.selectProduct(product)
                                navController.navigate("detail_screen")
                            }
                        }
                    }
                }
            }
        }
    }
}
