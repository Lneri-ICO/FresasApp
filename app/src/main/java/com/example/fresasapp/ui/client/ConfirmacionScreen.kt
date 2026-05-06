package com.example.fresasapp.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.data.model.Producto
import com.example.fresasapp.viewmodel.PedidoState
import com.example.fresasapp.viewmodel.PedidoViewModel

@Composable
fun ConfirmacionScreen(
    carrito: List<Producto>,
    tipoEntrega: String,
    onIrAlInicio: () -> Unit,
    viewModel: PedidoViewModel = viewModel()
) {
    val pedidoState by viewModel.pedidoState.collectAsState()

    // Crear pedido automáticamente al entrar
    LaunchedEffect(Unit) {
        viewModel.crearPedido(carrito, tipoEntrega)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (pedidoState) {
            is PedidoState.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFFE91E63))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Guardando tu pedido...", color = Color.Gray)
                }
            }

            is PedidoState.Success -> {
                val pedido = (pedidoState as PedidoState.Success).pedido
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅", fontSize = 80.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "¡Pedido Confirmado!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tu pedido ha sido recibido y está siendo preparado 🍓",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Resumen del pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("📋 ID: ${pedido.id.take(8).uppercase()}...")
                            Text("🚚 Entrega: ${if (tipoEntrega == "delivery") "Delivery a domicilio" else "Recoger en tienda"}")
                            Text("💰 Total: $${pedido.total}")
                            Text("📦 Estado: Recibido")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onIrAlInicio,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            is PedidoState.Error -> {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Error al crear el pedido", fontSize = 18.sp, color = Color.Red)
                    Text((pedidoState as PedidoState.Error).mensaje, color = Color.Gray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onIrAlInicio, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))) {
                        Text("Regresar")
                    }
                }
            }

            else -> {}
        }
    }
}