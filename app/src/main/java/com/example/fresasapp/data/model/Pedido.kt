package com.example.fresasapp.data.model

data class Pedido(
    val id: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val items: List<ItemPedido> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "recibido",
    val tipoEntrega: String = "",
    val direccion: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)

data class ItemPedido(
    val productoId: String = "",
    val nombre: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0
)