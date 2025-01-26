package com.example.fakestore.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.fakestore.R

@Composable
fun HomeScreen( onProductClick: (Product) -> Unit) {
    var searchQuery by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf("All") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                ,
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
            trailingIcon = { IconButton(onClick = {}){
                Image(painterResource(R.drawable.mic), contentDescription = null,)
            } },
            shape = RoundedCornerShape(20.dp)

        )


    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$${product.price}", color = Color.Green, fontSize = 14.sp)
            }
        }
    }
}

// Sample Data Class
data class Product(val title: String, val price: Double, val category: String, val imageUrl: String)

@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen() {
    val sampleProducts = listOf(
        Product("Product 1", 29.99, "Electronics", "https://via.placeholder.com/150"),
        Product("Product 2", 49.99, "Clothing", "https://via.placeholder.com/150"),
        Product("Product 3", 19.99, "Books", "https://via.placeholder.com/150")
    )
    val categories = listOf("All", "Electronics", "Clothing", "Books")

}
