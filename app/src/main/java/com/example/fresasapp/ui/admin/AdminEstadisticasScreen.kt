package com.example.fresasapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun AdminEstadisticasScreen(
    onRegresar: () -> Unit,
    viewModel: PedidoViewModel = viewModel()
) {
    val pedidos by viewModel.pedidos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerPedidos()
    }

    // Calcular estadísticas
    val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val pedidosHoy = pedidos.filter { pedido ->
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(pedido.fechaCreacion)) == hoy
    }
    val ventasHoy = pedidosHoy.sumOf { it.total }
    val ventasTotales = pedidos.sumOf { it.total }
    val pedidosPendientes = pedidos.count { it.estado == "recibido" || it.estado == "preparando" }
    val pedidosEntregados = pedidos.count { it.estado == "entregado" }

    // Productos más vendidos
    val productosMasVendidos = pedidos
        .flatMap { it.items }
        .groupBy { it.nombre }
        .map { (nombre, items) -> nombre to items.sumOf { it.cantidad } }
        .sortedByDescending { it.second }
        .take(5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Estadísticas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF880E4F),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjetas de resumen
            item {
                Text("Resumen del día", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EstadisticaCard(
                        modifier = Modifier.weight(1f),
                        emoji = "📦",
                        titulo = "Pedidos hoy",
                        valor = "${pedidosHoy.size}",
                        color = Color(0xFF1976D2)
                    )
                    EstadisticaCard(
                        modifier = Modifier.weight(1f),
                        emoji = "💰",
                        titulo = "Ventas hoy",
                        valor = "$$ventasHoy",
                        color = Color(0xFF388E3C)
                    )
                }
            }

            item {
                Text("Resumen general", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EstadisticaCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🛒",
                        titulo = "Total pedidos",
                        valor = "${pedidos.size}",
                        color = Color(0xFF880E4F)
                    )
                    EstadisticaCard(
                        modifier = Modifier.weight(1f),
                        emoji = "💵",
                        titulo = "Ventas totales",
                        valor = "$$ventasTotales",
                        color = Color(0xFF880E4F)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EstadisticaCard(
                        modifier = Modifier.weight(1f),
                        emoji = "⏳",
                        titulo = "Pendientes",
                        valor = "$pedidosPendientes",
                        color = Color(0xFFF57C00)
                    )
                    EstadisticaCard(
                        modifier = Modifier.weight(1f),
                        emoji = "✅",
                        titulo = "Entregados",
                        valor = "$pedidosEntregados",
                        color = Color(0xFF388E3C)
                    )
                }
            }

            // Productos más vendidos
            item {
                Text("🏆 Productos más vendidos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (productosMasVendidos.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay datos aún", color = Color.Gray)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            productosMasVendidos.forEachIndexed { index, (nombre, cantidad) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when (index) {
                                                0 -> "🥇"
                                                1 -> "🥈"
                                                2 -> "🥉"
                                                else -> "  ${index + 1}."
                                            },
                                            fontSize = 20.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(nombre, fontWeight = FontWeight.Medium)
                                    }
                                    Surface(
                                        color = Color(0xFF880E4F).copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            "$cantidad vendidos",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            color = Color(0xFF880E4F),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (index < productosMasVendidos.size - 1) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Estados de pedidos
            item {
                Text("📈 Estado de pedidos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        listOf(
                            "recibido" to Color(0xFF1976D2),
                            "preparando" to Color(0xFFF57C00),
                            "listo" to Color(0xFF388E3C),
                            "entregado" to Color(0xFF616161)
                        ).forEach { (estado, color) ->
                            val cantidad = pedidos.count { it.estado == estado }
                            val porcentaje = if (pedidos.isEmpty()) 0f
                            else cantidad.toFloat() / pedidos.size.toFloat()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    estado.replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.width(100.dp),
                                    fontSize = 13.sp
                                )
                                LinearProgressIndicator(
                                    progress = { porcentaje },
                                    modifier = Modifier.weight(1f).height(8.dp),
                                    color = color,
                                    trackColor = color.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "$cantidad",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticaCard(
    modifier: Modifier = Modifier,
    emoji: String,
    titulo: String,
    valor: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(titulo, fontSize = 12.sp, color = Color.Gray)
        }
    }
}