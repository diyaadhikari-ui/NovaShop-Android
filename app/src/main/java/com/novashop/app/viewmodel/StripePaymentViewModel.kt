package com.novashop.app.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.novashop.app.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel specifically for handling Stripe payment processing
 * Works in conjunction with OrderViewModel for complete order management
 */
class StripePaymentViewModel : ViewModel() {

    // Firebase Database Reference
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ordersRef: DatabaseReference = database.getReference("orders")

    // Payment state flows
    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.IDLE)
    val paymentStatus: StateFlow<PaymentStatus> = _paymentStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _paymentAmount = MutableStateFlow(0.0)
    val paymentAmount: StateFlow<Double> = _paymentAmount.asStateFlow()

    private var paymentListener: ValueEventListener? = null
    private var currentOrderId: String = ""

    // ======================== PAYMENT INITIATION ========================

    /**
     * Initiate Stripe payment for an order
     * Creates order in Firebase and opens Stripe payment link
     *
     * @param order The order to process
     * @param stripePaymentLink Your Stripe payment link
     */
    fun initiateStripePayment(order: Order, stripePaymentLink: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _paymentStatus.value = PaymentStatus.PROCESSING
            _errorMessage.value = ""

            try {
                // Generate order ID
                val orderId = ordersRef.push().key ?: run {
                    _errorMessage.value = "Failed to generate order ID"
                    _isLoading.value = false
                    _paymentStatus.value = PaymentStatus.FAILED
                    return@launch
                }

                currentOrderId = orderId

                // Create order with pending payment status
                val orderWithId = order.copy(
                    id = orderId,
                    status = "pending_payment",
                    paymentMethod = "stripe",
                    paymentStatus = "unpaid"
                )

                // Save order to Firebase
                ordersRef.child(orderId).setValue(orderWithId)
                    .addOnSuccessListener {
                        _paymentAmount.value = order.totalAmount

                        // Start listening for payment status
                        listenToPaymentStatus(orderId)

                        // Emit payment link to open
                        _paymentUrl.value = stripePaymentLink
                    }
                    .addOnFailureListener { exception ->
                        _isLoading.value = false
                        _paymentStatus.value = PaymentStatus.FAILED
                        _errorMessage.value = "Failed to create order: ${exception.message}"
                    }
            } catch (e: Exception) {
                _isLoading.value = false
                _paymentStatus.value = PaymentStatus.FAILED
                _errorMessage.value = "Error initiating payment: ${e.message}"
            }
        }
    }

    // ======================== PAYMENT STATUS MONITORING ========================

    /**
     * Listen to Firebase for real-time payment status updates
     *
     * @param orderId The order ID to monitor
     */
    private fun listenToPaymentStatus(orderId: String) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val paymentStatusValue = snapshot.child("paymentStatus").value as? String

                when (paymentStatusValue) {
                    "paid" -> {
                        _isLoading.value = false
                        _paymentStatus.value = PaymentStatus.SUCCESS
                        _errorMessage.value = ""
                        stopListeningToPaymentStatus(orderId)
                    }
                    "failed" -> {
                        _isLoading.value = false
                        _paymentStatus.value = PaymentStatus.FAILED
                        _errorMessage.value = "Payment was declined. Please try again."
                        stopListeningToPaymentStatus(orderId)
                    }
                    "cancelled" -> {
                        _isLoading.value = false
                        _paymentStatus.value = PaymentStatus.CANCELLED
                        _errorMessage.value = "Payment was cancelled."
                        stopListeningToPaymentStatus(orderId)
                    }
                    "pending" -> {
                        _paymentStatus.value = PaymentStatus.PROCESSING
                    }
                    // For "unpaid" and other states, keep listening
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _paymentStatus.value = PaymentStatus.FAILED
                _errorMessage.value = "Error monitoring payment: ${error.message}"
                stopListeningToPaymentStatus(orderId)
            }
        }

        paymentListener = listener
        ordersRef.child(orderId).addValueEventListener(listener)
    }

    /**
     * Stop listening to payment status changes
     */
    private fun stopListeningToPaymentStatus(orderId: String) {
        paymentListener?.let {
            ordersRef.child(orderId).removeEventListener(it)
        }
        paymentListener = null
    }

    // ======================== PAYMENT ACTIONS ========================

    /**
     * Clear payment URL after it's been used
     * Call this after opening the payment link
     */
    fun clearPaymentUrl() {
        _paymentUrl.value = null
    }

    /**
     * Retry failed payment
     */
    fun retryPayment(stripePaymentLink: String) {
        if (currentOrderId.isNotEmpty()) {
            _paymentStatus.value = PaymentStatus.PROCESSING
            _errorMessage.value = ""
            _paymentUrl.value = stripePaymentLink
        }
    }

    /**
     * Reset payment state to initial state
     */
    fun resetPaymentState() {
        _paymentUrl.value = null
        _paymentStatus.value = PaymentStatus.IDLE
        _isLoading.value = false
        _errorMessage.value = ""
        _paymentAmount.value = 0.0
        currentOrderId = ""
        paymentListener?.let {
            ordersRef.removeEventListener(it)
        }
        paymentListener = null
    }

    // ======================== UTILITY METHODS ========================

    /**
     * Get the current order ID
     */
    fun getCurrentOrderId(): String = currentOrderId

    /**
     * Check if payment is successful
     */
    fun isPaymentSuccessful(): Boolean = _paymentStatus.value == PaymentStatus.SUCCESS

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = ""
    }

    // ======================== CLEANUP ========================

    override fun onCleared() {
        super.onCleared()
        stopListeningToPaymentStatus(currentOrderId)
    }
}

/**
 * Enum for payment status tracking
 */
enum class PaymentStatus {
    IDLE,           // Initial state, no payment in progress
    PROCESSING,     // Payment is being processed
    PENDING,        // Waiting for webhook confirmation
    SUCCESS,        // Payment successful
    FAILED,         // Payment failed
    CANCELLED       // User cancelled payment
}