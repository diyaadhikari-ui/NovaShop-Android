package com.novashop.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novashop.app.data.model.CartItem
import com.novashop.app.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val repository = CartRepository()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val subtotal: Double
        get() = _cartItems.value.sumOf { it.totalPrice }

    val itemCount: Int
        get() = _cartItems.value.sumOf { it.quantity }

    fun loadCart(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getCartItems(userId)

            if (result.isSuccess) {
                _cartItems.value = result.getOrNull() ?: emptyList()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message
            }

            _isLoading.value = false
        }
    }

    fun addToCart(cartItem: CartItem, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val fixedItem = cartItem.copy(
                userId = userId,
                totalPrice = cartItem.price * cartItem.quantity
            )

            val result = repository.addToCart(fixedItem)

            if (result.isSuccess) {
                _error.value = null
                loadCart(userId)
            } else {
                _error.value = result.exceptionOrNull()?.message
            }

            _isLoading.value = false
        }
    }

    fun updateQuantity(
        cartItemId: String,
        quantity: Int,
        price: Double,
        userId: String
    ) {
        viewModelScope.launch {
            val result = repository.updateQuantity(cartItemId, quantity, price)

            if (result.isSuccess) {
                loadCart(userId)
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun removeFromCart(cartItemId: String, userId: String) {
        viewModelScope.launch {
            val result = repository.removeFromCart(cartItemId)

            if (result.isSuccess) {
                loadCart(userId)
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearCart(userId: String) {
        viewModelScope.launch {
            val result = repository.clearCart(userId)

            if (result.isSuccess) {
                _cartItems.value = emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }
}