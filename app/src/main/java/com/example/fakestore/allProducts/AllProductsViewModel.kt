package com.example.fakestore.allProducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.model.AllProducts
import com.example.fakestore.model.Category
import com.example.fakestore.model.ProductItem
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
class AllProductsViewModel @Inject constructor(
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

    private val _allCategories = MutableStateFlow<Resource<Category>>(Resource.Idle)
    val allCategories = _allCategories.asStateFlow()

    private val _searchedProduct =MutableStateFlow<Resource<ProductItem>>(Resource.Idle)
    val searchedProduct=_searchedProduct.asStateFlow()

    init {
        getAllProducts()
        getAllProductsCategory()
    }

    fun getAllProducts() {
        viewModelScope.launch {
            networkRepository.getAllProducts()
                .catch { e ->
                    _allProducts.value =
                        Resource.Error("Failed to fetch products: ${e.localizedMessage}")
                }
                .collect {
                    _allProducts.value = it
                }
        }
    }


    private fun getAllProductsCategory() {
        viewModelScope.launch {
            networkRepository.getAllCategories()
                .catch { e ->
                    _allCategories.value =
                        Resource.Error("Failed to fetch products: ${e.localizedMessage}")
                }
                .collect {
                    _allCategories.value = it
                }
        }
    }

    fun getProductsByCategory(category: String) {
        viewModelScope.launch {
            networkRepository.categoriesByName(category)
                .catch { e ->
                    _allProducts.value =
                        Resource.Error("Failed to fetch products: ${e.localizedMessage}")
                }
                .collect {
                    _allProducts.value = it
                }
        }
    }

    fun searchedProduct(id: Int) {

        viewModelScope.launch {
            _searchedProduct.value = Resource.Loading
            networkRepository.getAllProductById(id)
                .catch {
                    _searchedProduct.value =
                        Resource.Error("Failed to fetch products: ${it.localizedMessage}")
                }
                .collect {
                   _searchedProduct.value=it
                }

        }

    }
}