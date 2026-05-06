package com.example.fresasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fresasapp.data.model.Producto
import com.example.fresasapp.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {
    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun obtenerProductos() {
        viewModelScope.launch {
            _cargando.value = true
            val result = repository.obtenerProductos()
            if (result.isSuccess) {
                _productos.value = result.getOrNull() ?: emptyList()
            }
            _cargando.value = false
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _cargando.value = true
            val result = repository.agregarProducto(producto)
            _mensaje.value = if (result.isSuccess) "Producto agregado ✅" else "Error al agregar ❌"
            obtenerProductos()
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _cargando.value = true
            val result = repository.actualizarProducto(producto)
            _mensaje.value = if (result.isSuccess) "Producto actualizado ✅" else "Error al actualizar ❌"
            obtenerProductos()
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            val result = repository.eliminarProducto(productoId)
            _mensaje.value = if (result.isSuccess) "Producto eliminado ✅" else "Error al eliminar ❌"
            obtenerProductos()
        }
    }

    fun toggleDisponibilidad(producto: Producto) {
        viewModelScope.launch {
            repository.toggleDisponibilidad(producto)
            obtenerProductos()
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}