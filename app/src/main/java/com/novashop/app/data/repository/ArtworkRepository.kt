package com.novashop.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.novashop.app.data.model.Artwork
import com.novashop.app.data.model.Category
import kotlinx.coroutines.tasks.await

class ArtworkRepository {

    private val db = FirebaseFirestore.getInstance()
    private val artworksCollection = db.collection("artworks")
    private val categoriesCollection = db.collection("categories")

    private fun mapArtwork(doc: com.google.firebase.firestore.DocumentSnapshot): Artwork? {
        return doc.toObject(Artwork::class.java)?.copy(id = doc.id)
    }

    suspend fun getAllArtworks(): Result<List<Artwork>> {
        return try {
            val snapshot = artworksCollection.get().await()

            val artworks = snapshot.documents.mapNotNull { doc ->
                mapArtwork(doc)
            }

            Log.d("ArtworkRepository", "Got ${artworks.size} artworks")
            Result.success(artworks)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error fetching artworks", e)
            Result.failure(e)
        }
    }

    suspend fun getFeaturedArtworks(): Result<List<Artwork>> {
        return try {
            val snapshot = artworksCollection
                .whereEqualTo("isFeatured", true)
                .get()
                .await()

            val artworks = snapshot.documents.mapNotNull { doc ->
                mapArtwork(doc)
            }

            Log.d("ArtworkRepository", "Got ${artworks.size} featured artworks")
            Result.success(artworks)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error fetching featured artworks", e)
            Result.failure(e)
        }
    }

    suspend fun getArtworksByCategory(category: String): Result<List<Artwork>> {
        return try {
            val snapshot = artworksCollection
                .whereEqualTo("categoryName", category)
                .get()
                .await()

            val artworks = snapshot.documents.mapNotNull { doc ->
                mapArtwork(doc)
            }

            Result.success(artworks)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error fetching category artworks", e)
            Result.failure(e)
        }
    }

    suspend fun getArtworkById(id: String): Result<Artwork> {
        return try {
            val doc = artworksCollection.document(id).get().await()

            val artwork = doc.toObject(Artwork::class.java)
                ?.copy(id = doc.id)
                ?: Artwork()

            Result.success(artwork)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error fetching artwork by id", e)
            Result.failure(e)
        }
    }

    suspend fun addArtwork(artwork: Artwork): Result<String> {
        return try {
            val docRef = artworksCollection.add(artwork).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error adding artwork", e)
            Result.failure(e)
        }
    }

    suspend fun updateArtwork(artwork: Artwork): Result<Unit> {
        return try {
            if (artwork.id.isBlank()) {
                return Result.failure(Exception("Artwork id is empty"))
            }

            artworksCollection
                .document(artwork.id)
                .set(artwork)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error updating artwork", e)
            Result.failure(e)
        }
    }

    suspend fun deleteArtwork(id: String): Result<Unit> {
        return try {
            artworksCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error deleting artwork", e)
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val snapshot = categoriesCollection.get().await()
            val categories = snapshot.toObjects(Category::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error fetching categories", e)
            Result.failure(e)
        }
    }

    suspend fun searchArtworks(query: String): Result<List<Artwork>> {
        return try {
            val snapshot = artworksCollection.get().await()

            val artworks = snapshot.documents.mapNotNull { doc ->
                mapArtwork(doc)
            }.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.categoryName.contains(query, ignoreCase = true)
            }

            Result.success(artworks)
        } catch (e: Exception) {
            Log.e("ArtworkRepository", "Error searching artworks", e)
            Result.failure(e)
        }
    }
}