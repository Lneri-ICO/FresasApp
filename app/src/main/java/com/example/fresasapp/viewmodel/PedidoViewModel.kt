package com.example.fresasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fresasapp.data.model.Pedido
import com.example.fresasapp.data.model.Producto
import com.example.fresasapp.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PedidoState {
    object Idle : PedidoState()
    object Loading : PedidoState()
    data class Success(val pedido: Pedido) : PedidoState()
    data class Error(val mensaje: String) : PedidoState()
}

class PedidoViewModel : ViewModel() {
    private val repository = PedidoRepository()

    private val _pedidoState = MutableStateFlow<PedidoState>(PedidoState.Idle)
    val pedidoState: StateFlow<PedidoState> = _pedidoState

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    private val _pedidosCliente = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosCliente: StateFlow<List<Pedido>> = _pedidosCliente

    fun crearPedido(productos: List<Producto>, tipoEntrega: String, direccion: String = "") {
        viewModelScope.launch {
            _pedidoState.value = PedidoState.Loading
            val result = repository.crearPedido(productos, tipoEntrega, direccion)
            _pedidoState.value = if (result.isSuccess) {
                PedidoState.Success(result.getOrNull()!!)
            } else {
                PedidoState.Error(result.exceptionOrNull()?.message ?: "Error al crear pedido")
            }
        }
    }

    fun obtenerPedidos() {
        viewModelScope.launch {
            val result = repository.obtenerPedidos()
            if (result.isSuccess) {
                _pedidos.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    fun obtenerPedidosCliente(clienteId: String) {
        viewModelScope.launch {
            val result = repository.obtenerPedidosCliente(clienteId)
            if (result.isSuccess) {
                _pedidosCliente.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    fun actualizarEstado(pedidoId: String, nuevoEstado: String) {
        viewModelScope.launch {
            repository.actualizarEstado(pedidoId, nuevoEstado)
            obtenerPedidos()
        }
    }
}