package com.novashop.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.novashop.app.data.model.CartItem
import kotlinx.coroutines.tasks.await

class CartRepository {

    private val db = FirebaseFirestore.getInstance()
    private val cartCollection = db.collection("cart")

    suspend fun getCartItems(userId: String): Result<List<CartItem>> {
        return try {
            val snapshot = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { document ->
                val item = document.toObject(CartItem::class.java)
                item?.copy(id = document.id)
            }

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(cartItem: CartItem): Result<String> {
        return try {
            val existing = cartCollection
                .whereEqualTo("userId", cartItem.userId)
                .whereEqualTo("artworkId", cartItem.artworkId)
                .get()
                .await()

            if (existing.documents.isNotEmpty()) {
                val doc = existing.documents.first()

                val currentQty = (doc.getLong("quantity") ?: 1L).toInt()
                val newQty = currentQty + cartItem.quantity
                val newTotal = cartItem.price * newQty

                cartCollection.document(doc.id)
                    .update(
                        mapOf(
                            "quantity" to newQty,
                            "totalPrice" to newTotal
                        )
                    )
                    .await()

                Result.success(doc.id)
            } else {
                val docRef = cartCollection.document()

                val newItem = cartItem.copy(
                    id = docRef.id,
                    totalPrice = cartItem.price * cartItem.quantity
                )

                docRef.set(newItem).await()

                Result.success(docRef.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(
        cartItemId: String,
        quantity: Int,
        price: Double
    ): Result<Unit> {
        return try {
            val newTotal = price * quantity

            cartCollection.document(cartItemId)
                .update(
                    mapOf(
                        "quantity" to quantity,
                        "totalPrice" to newTotal
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        return try {
            cartCollection.document(cartItemId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val snapshot = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch = db.batch()

            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}