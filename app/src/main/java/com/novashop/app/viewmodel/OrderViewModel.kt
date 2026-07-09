package com.novashop.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novashop.app.data.model.Order
import com.novashop.app.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _orderPlaced = MutableStateFlow(false)
    val orderPlaced: StateFlow<Boolean> = _orderPlaced

    fun createOrder(order: Order) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createOrder(order)
            if (result.isSuccess) {
                _orderPlaced.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun loadMyOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getMyOrders(userId)
            if (result.isSuccess) {
                _orders.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllOrders()
            if (result.isSuccess) {
                _orders.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(orderId, status)
            if (result.isSuccess) {
                loadAllOrders()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun selectOrder(order: Order) {
        _selectedOrder.value = order
    }

    fun resetOrderPlaced() {
        _orderPlaced.value = false
    }
}