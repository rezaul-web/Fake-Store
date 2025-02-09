package com.example.fakestore.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val uuid: String? = firebaseAuth.currentUser?.uid
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems
    private val _totalPrice =MutableStateFlow(0)
    val totalPrice=_totalPrice
    private val _totalQuantity =MutableStateFlow(0)
    val totalQuantity=_totalQuantity

    init {
        _totalQuantity.value=0
        _totalPrice.value=0
        getCartItems()
    }


    fun addToCart(productId: String, name: String, price: Double, quantity: Int, imageUrl: String) {
        if (uuid == null) return

        viewModelScope.launch {
            try {
                val cartRef = firestore.collection("users")
                    .document(uuid)
                    .collection("cart")

                val querySnapshot = cartRef.whereEqualTo("productId", productId).get().await()

                if (querySnapshot.isEmpty) {
                    val productData = mapOf(
                        "productId" to productId,
                        "name" to name,
                        "price" to price,
                        "quantity" to quantity,
                        "imageUrl" to imageUrl
                    )
                    cartRef.add(productData)
                } else {
                    val documentId = querySnapshot.documents.first().id
                    val currentQuantity = querySnapshot.documents.first().getLong("quantity") ?: 0
                    cartRef.document(documentId).update("quantity", currentQuantity + 1)
                }
            } catch (_: Exception) {
            }
        }
    }
    private fun getCartItems() {
        if (uuid == null) {
            Log.e("CartViewModel", "User is not authenticated")
            return
        }

        val cartRef = firestore.collection("users")
            .document(uuid)
            .collection("cart")

        cartRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e("CartViewModel", "Error fetching cart items: $exception")
                return@addSnapshotListener
            }

            val items = querySnapshot?.documents?.map { document ->
                CartItem(
                    productId = document.getString("productId") ?: "",
                    name = document.getString("name") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    quantity = document.getLong("quantity")?.toInt() ?: 0,
                    imageUrl = document.getString("imageUrl") ?: ""
                )
            } ?: emptyList()

            _cartItems.value = items

            // ✅ Reset before updating to avoid accumulation
            _totalPrice.value = 0
            _totalQuantity.value = 0

            items.forEach { item ->
                _totalPrice.value += (item.price.roundToInt() * item.quantity)
                _totalQuantity.value += item.quantity
            }

            Log.d("CartViewModel", "Cart items updated: $items")
        }
    }

    fun deleteFromCart(productId: String) {
        if (uuid == null) {
            Log.e("CartViewModel", "User is not authenticated")
            return
        }

        viewModelScope.launch {
            try {
                val cartRef = firestore.collection("users")
                    .document(uuid)
                    .collection("cart")

                val querySnapshot = cartRef.whereEqualTo("productId",productId).get().await()

                if (querySnapshot.isEmpty) {

                    return@launch
                }

                val documentId = querySnapshot.documents.first().id

                cartRef.document(documentId).delete().await()
                Log.d("deletedCart","yes")
            } catch (e: Exception) {

            }
            getCartItems()
        }

    }



    fun updateCartItemQuantity(productId: String, increment: Boolean) {
        if (uuid == null) return

        viewModelScope.launch {
            try {
                val cartRef = firestore.collection("users")
                    .document(uuid)
                    .collection("cart")

                val querySnapshot = cartRef.whereEqualTo("productId", productId).get().await()

                if (querySnapshot.isEmpty) return@launch

                val documentId = querySnapshot.documents.first().id
                val currentQuantity = querySnapshot.documents.first().getLong("quantity") ?: 0

                if (increment) {
                    cartRef.document(documentId).update("quantity", currentQuantity + 1)
                } else {
                    if (currentQuantity > 1) {
                        cartRef.document(documentId).update("quantity", currentQuantity - 1)
                    } else {
                        cartRef.document(documentId).delete().await()
                    }
                }
            } catch (_: Exception) {
            }
        }
        getCartItems()
    }
   
}


