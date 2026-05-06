package com.example.fresasapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.data.model.Pedido
import com.example.fresasapp.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onCerrarSesion: () -> Unit,
    onIrAProductos: () -> Unit,
    onIrAEstadisticas: () -> Unit,
    viewModel: PedidoViewModel = viewModel()
) {
    val pedidos by viewModel.pedidos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerPedidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🔧 Panel Admin", fontWeight = FontWeight.Bold)
                        Text("FresasApp", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF880E4F),
                    titleContentColor = Color.White
                ),
                actions = {
                    TextButton(onClick = onIrAEstadisticas) {
                        Text("📊", color = Color.White, fontSize = 20.sp)
                    }
                    TextButton(onClick = onIrAProductos) {
                        Text("🛍️", color = Color.White, fontSize = 20.sp)
                    }
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->

        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay pedidos aún", fontSize = 18.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.obtenerPedidos() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF880E4F))
                    ) {
                        Text("Actualizar")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Pedidos (${pedidos.size})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { viewModel.obtenerPedidos() }) {
                            Text("↻ Actualizar", color = Color(0xFF880E4F))
                        }
                    }
                }

                items(pedidos) { pedido ->
                    PedidoAdminCard(
                        pedido = pedido,
                        onCambiarEstado = { nuevoEstado ->
                            viewModel.actualizarEstado(pedido.id, nuevoEstado)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PedidoAdminCard(
    pedido: Pedido,
    onCambiarEstado: (String) -> Unit
) {
    val coloresEstado = mapOf(
        "recibido" to Color(0xFF1976D2),
        "preparando" to Color(0xFFF57C00),
        "listo" to Color(0xFF388E3C),
        "entregado" to Color(0xFF616161)
    )
    val emojisEstado = mapOf(
        "recibido" to "📥",
        "preparando" to "👨‍🍳",
        "listo" to "✅",
        "entregado" to "🎉"
    )

    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(pedido.fechaCreacion))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pedido #${pedido.id.take(6).uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Surface(
                    color = coloresEstado[pedido.estado] ?: Color.Gray,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "${emojisEstado[pedido.estado]} ${pedido.estado.replaceFirstChar { it.uppercase() }}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("👤 ${pedido.clienteNombre}", fontSize = 14.sp)
            Text("🕐 $fecha", fontSize = 13.sp, color = Color.Gray)
            Text(
                "🚚 ${if (pedido.tipoEntrega == "delivery") "Delivery" else "Recoger en tienda"}",
                fontSize = 13.sp
            )
            if (pedido.direccion.isNotEmpty()) {
                Text("📍 ${pedido.direccion}", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Productos:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            pedido.items.forEach { item ->
                Text("  • ${item.nombre} x${item.cantidad} — $${item.precio}", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "$${pedido.total}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF880E4F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (pedido.estado != "entregado") {
                val siguienteEstado = when (pedido.estado) {
                    "recibido" -> "preparando"
                    "preparando" -> "listo"
                    "listo" -> "entregado"
                    else -> ""
                }
                val textoBoton = when (pedido.estado) {
                    "recibido" -> "👨‍🍳 Iniciar preparación"
                    "preparando" -> "✅ Marcar como listo"
                    "listo" -> "🎉 Marcar como entregado"
                    else -> ""
                }

                Button(
                    onClick = { onCambiarEstado(siguienteEstado) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = coloresEstado[siguienteEstado] ?: Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(textoBoton, fontSize = 14.sp)
                }
            }
        }
    }
}