package com.example.fresasapp.data.repository

import com.example.fresasapp.data.model.ItemPedido
import com.example.fresasapp.data.model.Pedido
import com.example.fresasapp.data.model.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PedidoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun crearPedido(
        productos: List<Producto>,
        tipoEntrega: String,
        direccion: String = ""
    ): Result<Pedido> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No hay usuario")
            val nombreUsuario = auth.currentUser?.email ?: "Cliente"

            val items = productos.map {
                ItemPedido(
                    productoId = it.id,
                    nombre = it.nombre,
                    cantidad = 1,
                    precio = it.precio
                )
            }

            val total = productos.sumOf { it.precio }
            val pedidoRef = db.collection("pedidos").document()

            val pedido = Pedido(
                id = pedidoRef.id,
                clienteId = uid,
                clienteNombre = nombreUsuario,
                items = items,
                total = total,
                estado = "recibido",
                tipoEntrega = tipoEntrega,
                direccion = direccion,
                fechaCreacion = System.currentTimeMillis()
            )

            pedidoRef.set(pedido).await()
            Result.success(pedido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPedidos(): Result<List<Pedido>> {
        return try {
            val snapshot = db.collection("pedidos")
                .orderBy("fechaCreacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val pedidos = snapshot.toObjects(Pedido::class.java)
            Result.success(pedidos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPedidosCliente(clienteId: String): Result<List<Pedido>> {
        return try {
            val snapshot = db.collection("pedidos")
                .whereEqualTo("clienteId", clienteId)
                .orderBy("fechaCreacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val pedidos = snapshot.toObjects(Pedido::class.java)
            Result.success(pedidos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEstado(pedidoId: String, nuevoEstado: String): Result<Unit> {
        return try {
            val doc = db.collection("pedidos").document(pedidoId).get().await()
            val clienteId = doc.getString("clienteId") ?: ""

            db.collection("pedidos").document(pedidoId)
                .update("estado", nuevoEstado).await()

            enviarNotificacion(clienteId, nuevoEstado)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enviarNotificacion(clienteId: String, estado: String): Result<Unit> {
        return try {
            val doc = db.collection("usuarios").document(clienteId).get().await()
            val token = doc.getString("fcmToken") ?: return Result.success(Unit)

            val mensaje = when (estado) {
                "preparando" -> "👨‍🍳 Tu pedido está siendo preparado"
                "listo" -> "✅ ¡Tu pedido está listo para recoger!"
                "entregado" -> "🎉 ¡Tu pedido fue entregado!"
                else -> "📦 Tu pedido fue actualizado"
            }

            db.collection("notificaciones").add(
                mapOf(
                    "token" to token,
                    "titulo" to "FresasApp 🍓",
                    "mensaje" to mensaje,
                    "estado" to estado,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}