package com.novashop.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.novashop.app.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                id = result.user?.uid ?: "",
                fullName = fullName,
                email = email,
                role = "customer",
                createdAt = System.currentTimeMillis()
            )
            db.collection("users")
                .document(user.id)
                .set(user)
                .await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: ""
            val doc = db.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: User()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: User()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(uid: String): String {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.getString("role") ?: "customer"
        } catch (e: Exception) {
            "customer"
        }
    }
}