package com.example.fresasapp.data.repository

import com.example.fresasapp.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerProductos(): Result<List<Producto>> {
        return try {
            val snapshot = db.collection("productos").get().await()
            val productos = snapshot.toObjects(Producto::class.java)
            Result.success(productos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agregarProducto(producto: Producto): Result<Unit> {
        return try {
            val ref = db.collection("productos").document()
            val productoConId = producto.copy(id = ref.id)
            ref.set(productoConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarProducto(producto: Producto): Result<Unit> {
        return try {
            db.collection("productos").document(producto.id).set(producto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarProducto(productoId: String): Result<Unit> {
        return try {
            db.collection("productos").document(productoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleDisponibilidad(producto: Producto): Result<Unit> {
        return try {
            db.collection("productos").document(producto.id)
                .update("disponible", !producto.disponible).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}