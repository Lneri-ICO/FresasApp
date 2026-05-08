package com.example.fresasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fresasapp.data.model.Resena
import com.example.fresasapp.data.repository.ResenaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResenaViewModel : ViewModel() {
    private val repository = ResenaRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas

    private val _promedio = MutableStateFlow(0.0)
    val promedio: StateFlow<Double> = _promedio

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun obtenerResenas(productoId: String) {
        viewModelScope.launch {
            val result = repository.obtenerResenasPorProducto(productoId)
            if (result.isSuccess) {
                _resenas.value = result.getOrNull() ?: emptyList()
            }
            val promedioResult = repository.obtenerPromedioCalificacion(productoId)
            if (promedioResult.isSuccess) {
                _promedio.value = promedioResult.getOrNull() ?: 0.0
            }
        }
    }

    fun agregarResena(productoId: String, calificacion: Int, comentario: String) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val nombre = auth.currentUser?.email ?: "Cliente"

            val resena = Resena(
                productoId = productoId,
                clienteId = uid,
                clienteNombre = nombre,
                calificacion = calificacion,
                comentario = comentario
            )

            val result = repository.agregarResena(resena)
            _mensaje.value = if (result.isSuccess) "✅ Reseña enviada" else "❌ Error al enviar"
            if (result.isSuccess) obtenerResenas(productoId)
        }
    }

    fun limpiarMensaje() { _mensaje.value = "" }
}