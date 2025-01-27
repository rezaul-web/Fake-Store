package com.example.fakestore.cart

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.models.allproducts.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val uuid: String? = firebaseAuth.currentUser?.uid
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems

    init {
        Log.d("CartViewModel", "User UID: $uuid")
        getCartItems()
    }

    // Function to add items to the cart
    fun addToCart(productId: String, name: String, price: Double, quantity: Int, imageUrl: String) {
        if (uuid == null) {
            Log.e("CartViewModel", "User is not authenticated")
            return
        }

        // Create a map of product data
        val productData = mapOf(
            "productId" to productId,
            "name" to name,
            "price" to price,
            "quantity" to quantity,
            "imageUrl" to imageUrl,
        )

        viewModelScope.launch {
            try {
                // Reference to the user's cart collection
                val cartRef = firestore.collection("users")
                    .document(uuid)
                    .collection("cart")

                // Add the product data to the cart
                cartRef.add(productData)
                    .addOnSuccessListener {
                        Log.d("CartViewModel", "Product added successfully: $productData")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("CartViewModel", "Failed to add product to cart", exception)
                    }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Unexpected error while adding product to cart", e)
            }
        }
    }

    // Function to get cart items with real-time updates
    fun getCartItems() {
        if (uuid == null) {
            Log.e("CartViewModel", "User is not authenticated")
            return
        }

        // Use Firestore's real-time listener (addSnapshotListener) for automatic updates
        val cartRef = firestore.collection("users")
            .document(uuid)
            .collection("cart")

        cartRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e("CartViewModel", "Error fetching cart items: $exception")
                return@addSnapshotListener
            }

            // Map the documents to CartItem objects
            val items = querySnapshot?.documents?.map { document ->
                CartItem(
                    productId = document.getString("productId") ?: "",
                    name = document.getString("name") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    quantity = document.getLong("quantity")?.toInt() ?: 0,
                    imageUrl = document.getString("imageUrl") ?: ""
                )
            } ?: emptyList()

            // Update the cart items list in the state
            _cartItems.value = items
            Log.d("CartViewModel", "Cart items updated: $items")
        }
    }
    // Function to delete an item from the cart
    fun deleteFromCart(cartItem: CartItem) {
        if (uuid == null) {
            Log.e("CartViewModel", "User is not authenticated")
            return
        }

        viewModelScope.launch {
            try {
                // Reference to the user's cart collection
                val cartRef = firestore.collection("users")
                    .document(uuid)
                    .collection("cart")

                // Find the document with the matching productId
                val querySnapshot = cartRef.whereEqualTo("productId", cartItem.productId).get().await()

                if (querySnapshot.isEmpty) {
                    Log.e("CartViewModel", "Cart item with productId ${cartItem.productId} not found")
                    return@launch
                }

                // Get the first document matching the productId
                val documentId = querySnapshot.documents.first().id

                // Delete the document using the actual Firestore document ID
                cartRef.document(documentId).delete().await()

                // Log success
                Log.d("CartViewModel", "Product deleted successfully: ${cartItem.name}")
            } catch (e: Exception) {
                Log.e("CartViewModel", "Unexpected error while deleting product from cart", e)
            }
        }

        // Refresh the cart items after deletion
        getCartItems()
    }


}


data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String

)