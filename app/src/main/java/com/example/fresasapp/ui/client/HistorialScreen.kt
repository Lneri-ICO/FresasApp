package com.example.fresasapp.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fresasapp.data.model.Pedido
import com.example.fresasapp.viewmodel.PedidoViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onRegresar: () -> Unit,
    viewModel: PedidoViewModel = viewModel()
) {
    val pedidos by viewModel.pedidosCliente.collectAsState()
    val clienteId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.obtenerPedidosCliente(clienteId)
    }

    LaunchedEffect(pedidos) {
        pedidos.forEach { pedido ->
            if (pedido.estado == "listo" || pedido.estado == "entregado") {
                val channelId = "fresasapp_channel"
                val notificationManager = context.getSystemService(
                    android.content.Context.NOTIFICATION_SERVICE
                ) as android.app.NotificationManager

                val mensaje = if (pedido.estado == "listo")
                    "✅ Tu pedido #${pedido.id.take(6).uppercase()} está listo"
                else
                    "🎉 Tu pedido #${pedido.id.take(6).uppercase()} fue entregado"

                val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Nay&Jos 🍓")
                    .setContentText(mensaje)
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(pedido.id.hashCode(), notification)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📋 Mis Pedidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE91E63),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tienes pedidos aún", fontSize = 18.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¡Haz tu primer pedido!", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRegresar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        )
                    ) {
                        Text("Ver productos")
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
                    Text(
                        "Total de pedidos: ${pedidos.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(pedidos) { pedido ->
                    PedidoClienteCard(pedido = pedido)
                }
            }
        }
    }
}

@Composable
fun PedidoClienteCard(pedido: Pedido) {
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

            Text("🕐 $fecha", fontSize = 13.sp, color = Color.Gray)
            Text(
                "🚚 ${if (pedido.tipoEntrega == "delivery") "Delivery a domicilio" else "Recoger en tienda"}",
                fontSize = 13.sp
            )
            if (pedido.direccion.isNotEmpty()) {
                Text("📍 ${pedido.direccion}", fontSize = 13.sp, color = Color.Gray)
            }

            // Tiempo estimado
            if (pedido.tiempoEstimado.isNotEmpty() &&
                (pedido.estado == "recibido" || pedido.estado == "preparando")
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFF1976D2).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⏱️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Tiempo estimado",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                pedido.tiempoEstimado,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Productos:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            pedido.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("  • ${item.nombre} x${item.cantidad}", fontSize = 13.sp)
                    Text("$${item.precio}", fontSize = 13.sp, color = Color.Gray)
                }
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
                    color = Color(0xFFE91E63)
                )
            }

            // Mensaje si el pedido está listo
            if (pedido.estado == "listo") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFF388E3C).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "✅ ¡Tu pedido está listo! Pasa a recogerlo 🍓",
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            if (pedido.estado == "entregado") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFF616161).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "🎉 ¡Tu pedido fue entregado! Gracias por tu compra",
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF616161),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}