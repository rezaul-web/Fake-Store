package com.example.fakestore.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.models.allproducts.AllProducts
import com.example.fakestore.models.allproducts.ProductItem
import com.example.fakestore.network.NetworkRepository
import com.example.fakestore.network.Resource
import com.example.fakestore.offer.DiscountedProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkRepository:
    NetworkRepository
) :
    ViewModel() {
    private val _selectedProduct = MutableStateFlow<ProductItem?>(null)
    val selectedProduct = _selectedProduct

    private val _selectedDiscountedProduct = MutableStateFlow<DiscountedProduct?>(null)
    val selectedDisCountedProduct = _selectedDiscountedProduct

    fun selectProduct(product: ProductItem) {
        _selectedProduct.value = product
    }
    fun selectDiscountedProduct(discountedProduct: DiscountedProduct) {
        _selectedDiscountedProduct.value = discountedProduct
    }

    private val _allProducts = MutableStateFlow<Resource<AllProducts>>(Resource.Idle)
    val allProducts = _allProducts.asStateFlow()
init {
    getAllProducts()
}
  fun getAllProducts() {
        viewModelScope.launch {
            networkRepository.getAllProducts()
                .catch { e ->
                    _allProducts.value = Resource.Error("Failed to fetch products: ${e.localizedMessage}")
                }
                .collect {
                    _allProducts.value = it
                }
        }
    }

}