package com.example.fakestore.cache

import com.example.fakestore.model.ProductItem

class LRUCache(private val maxSize: Int) {

    private val cache: LinkedHashMap<Int, ProductItem> = object : LinkedHashMap<Int, ProductItem>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, ProductItem>?): Boolean {
            return size > maxSize  // Remove least recently used when size exceeds maxSize
        }
    }

    fun get(key: Int): ProductItem? {
        return cache[key]
    }

    fun put(key: Int, value: ProductItem) {
        cache[key] = value
    }

    fun getAll(): List<ProductItem> {
        return cache.values.toList().reversed()  // Most recently used first
    }
}
