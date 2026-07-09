package com.novashop.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.novashop.app.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef = ordersCollection.document()

            val newOrder = order.copy(id = docRef.id)

            docRef.set(newOrder).await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                val order = doc.toObject(Order::class.java)
                order?.copy(id = doc.id)
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection.get().await()

            val orders = snapshot.documents.mapNotNull { doc ->
                val order = doc.toObject(Order::class.java)
                order?.copy(id = doc.id)
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(
        orderId: String,
        status: String
    ): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", status)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc = ordersCollection.document(orderId).get().await()

            val order = doc.toObject(Order::class.java)
                ?.copy(id = doc.id)
                ?: Order()

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}