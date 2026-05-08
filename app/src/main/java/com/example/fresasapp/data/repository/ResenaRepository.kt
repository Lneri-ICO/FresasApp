package com.example.fresasapp.data.repository

import com.example.fresasapp.data.model.Resena
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ResenaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun agregarResena(resena: Resena): Result<Unit> {
        return try {
            val ref = db.collection("resenas").document()
            val resenaConId = resena.copy(id = ref.id)
            ref.set(resenaConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerResenasPorProducto(productoId: String): Result<List<Resena>> {
        return try {
            val snapshot = db.collection("resenas")
                .whereEqualTo("productoId", productoId)
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val resenas = snapshot.toObjects(Resena::class.java)
            Result.success(resenas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPromedioCalificacion(productoId: String): Result<Double> {
        return try {
            val snapshot = db.collection("resenas")
                .whereEqualTo("productoId", productoId)
                .get().await()
            val resenas = snapshot.toObjects(Resena::class.java)
            val promedio = if (resenas.isEmpty()) 0.0
            else resenas.map { it.calificacion }.average()
            Result.success(promedio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}