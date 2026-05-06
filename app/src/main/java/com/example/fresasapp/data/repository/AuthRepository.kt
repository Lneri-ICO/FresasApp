package com.example.fresasapp.data.repository

import com.example.fresasapp.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String
    ): Result<Usuario> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Error al crear usuario")

            val usuario = Usuario(
                id = uid,
                nombre = nombre,
                email = email,
                telefono = telefono,
                rol = "cliente"
            )

            db.collection("usuarios").document(uid).set(usuario).await()

            // Guardar token FCM
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                db.collection("usuarios").document(uid).update("fcmToken", token)
            }

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Error al iniciar sesión")

            val doc = db.collection("usuarios").document(uid).get().await()
            val usuario = doc.toObject(Usuario::class.java)
                ?: throw Exception("Usuario no encontrado")

            // Actualizar token FCM
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                db.collection("usuarios").document(uid).update("fcmToken", token)
            }

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()

    fun usuarioActual() = auth.currentUser
}