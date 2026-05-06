package com.example.fresasapp.ui.client

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

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
                            Text(
                                "Resumen del pedido",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("📋 ID: #${pedido.id.take(8).uppercase()}")
                            Text(
                                "🚚 Entrega: ${if (tipoEntrega == "delivery") "Delivery a domicilio" else "Recoger en tienda"}"
                            )
                            Text("💰 Total: $${pedido.total}")
                            Text("📦 Estado: Recibido")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mensaje según tipo de entrega
                    Surface(
                        color = Color(0xFF1976D2).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (tipoEntrega == "delivery")
                                "🛵 Te avisaremos cuando tu pedido vaya en camino"
                            else
                                "🏪 Te avisaremos cuando tu pedido esté listo para recoger",
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFF1976D2),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onIrAlInicio,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Compartir por WhatsApp
                    OutlinedButton(
                        onClick = {
                            val texto = """
                                🍓 *FresasApp - Pedido Confirmado*
                                
                                📋 Pedido: #${pedido.id.take(8).uppercase()}
                                🚚 Entrega: ${if (tipoEntrega == "delivery") "Delivery" else "Recoger en tienda"}
                                💰 Total: $${pedido.total}
                                📦 Estado: Recibido
                                
                                ¡Gracias por tu pedido! 😊
                            """.trimIndent()

                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/?text=${Uri.encode(texto)}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF25D366)
                        )
                    ) {
                        Text(
                            "📲 Compartir por WhatsApp",
                            fontSize = 15.sp,
                            color = Color(0xFF25D366)
                        )
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
                    Text(
                        (pedidoState as PedidoState.Error).mensaje,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onIrAlInicio,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                    ) {
                        Text("Regresar")
                    }
                }
            }

            else -> {}
        }
    }
}