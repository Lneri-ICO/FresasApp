package com.example.fresasapp.data.model

data class Resena(
    val id: String = "",
    val productoId: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val calificacion: Int = 5,
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis()
)