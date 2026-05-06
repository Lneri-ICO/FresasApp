package com.example.fresasapp.data.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val rol: String = "cliente"
)