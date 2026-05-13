package com.example.fresasapp.ui.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.fresasapp.PagoConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    total: Double,
    pedidoId: String,
    onPagoCompletado: () -> Unit,
    onRegresar: () -> Unit
) {
    val context = LocalContext.current
    var metodoSeleccionado by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💳 Método de Pago", fontWeight = FontWeight.Bold) },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Total a pagar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE91E63).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total a pagar", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        "$$total",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63)
                    )
                    Text(
                        "Pedido #${pedidoId.take(8).uppercase()}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Selecciona tu método de pago",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Opción 1 — Efectivo
            MetodoPagoCard(
                emoji = "💵",
                titulo = "Pago en efectivo",
                descripcion = "Paga al momento de recoger o recibir tu pedido",
                color = Color(0xFF388E3C),
                seleccionado = metodoSeleccionado == "efectivo",
                onClick = { metodoSeleccionado = "efectivo" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opción 2 — SPEI
            MetodoPagoCard(
                emoji = "🏦",
                titulo = "Transferencia SPEI",
                descripcion = "Transfiere desde tu banco o app bancaria",
                color = Color(0xFF1976D2),
                seleccionado = metodoSeleccionado == "spei",
                onClick = { metodoSeleccionado = "spei" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opción 3 — MercadoPago (deshabilitado hasta tener token)
            MetodoPagoCard(
                emoji = "💳",
                titulo = "Tarjeta / MercadoPago",
                descripcion = if (PagoConfig.MERCADOPAGO_HABILITADO)
                    "Paga con tarjeta de crédito o débito"
                else
                    "Próximamente disponible",
                color = Color(0xFF009EE3),
                seleccionado = metodoSeleccionado == "mercadopago",
                onClick = {
                    if (PagoConfig.MERCADOPAGO_HABILITADO) {
                        metodoSeleccionado = "mercadopago"
                    } else {
                        Toast.makeText(
                            context,
                            "Próximamente disponible",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                deshabilitado = !PagoConfig.MERCADOPAGO_HABILITADO
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Detalle según método seleccionado
            when (metodoSeleccionado) {
                "efectivo" -> {
                    DetalleEfectivo(total = total)
                }
                "spei" -> {
                    DetalleSpei(
                        total = total,
                        context = context
                    )
                }
                "mercadopago" -> {
                    DetalleMercadoPago(
                        total = total,
                        pedidoId = pedidoId,
                        context = context
                    )
                }
            }

            if (metodoSeleccionado.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onPagoCompletado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        when (metodoSeleccionado) {
                            "efectivo" -> "✅ Confirmar pedido"
                            "spei" -> "✅ Ya realicé la transferencia"
                            "mercadopago" -> "✅ Ya realicé el pago"
                            else -> "Confirmar"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MetodoPagoCard(
    emoji: String,
    titulo: String,
    descripcion: String,
    color: Color,
    seleccionado: Boolean,
    onClick: () -> Unit,
    deshabilitado: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (seleccionado) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) color.copy(alpha = 0.1f) else Color.White
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (deshabilitado) Color.Gray else Color.Black
                )
                Text(
                    descripcion,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (seleccionado) {
                Text("✅", fontSize = 20.sp)
            }
            if (deshabilitado) {
                Surface(
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Pronto",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun DetalleEfectivo(total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF388E3C).copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "💵 Instrucciones de pago en efectivo",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF388E3C)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("• Ten listo el monto exacto: $$total")
            Text("• Paga al momento de recoger o al recibir tu pedido")
            Text("• Se te dará un comprobante de pago")
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFF388E3C).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "✅ Sin cargos adicionales",
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun DetalleSpei(total: Double, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1976D2).copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🏦 Datos para transferencia SPEI",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(12.dp))

            DatosBancarios(
                label = "Banco",
                valor = PagoConfig.BANCO
            )
            Spacer(modifier = Modifier.height(8.dp))

            DatosBancarios(
                label = "CLABE",
                valor = PagoConfig.CLABE,
                copiable = true,
                context = context
            )
            Spacer(modifier = Modifier.height(8.dp))

            DatosBancarios(
                label = "Titular",
                valor = PagoConfig.TITULAR
            )
            Spacer(modifier = Modifier.height(8.dp))

            DatosBancarios(
                label = "Monto",
                valor = "$$total"
            )
            Spacer(modifier = Modifier.height(8.dp))

            DatosBancarios(
                label = "Concepto",
                valor = PagoConfig.CONCEPTO,
                copiable = true,
                context = context
            )

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFF1976D2).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "⚠️ Envía tu comprobante por WhatsApp para confirmar tu pedido",
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFF1976D2),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón enviar comprobante por WhatsApp
            Button(
                onClick = {
                    val texto = """
                        🍓 *Nay&Jos - Comprobante de pago*
                        
                        💰 Monto: $$total
                        🏦 Transferencia SPEI realizada
                        
                        Adjunto mi comprobante de pago.
                    """.trimIndent()
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/?text=${Uri.encode(texto)}")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📲 Enviar comprobante por WhatsApp")
            }
        }
    }
}

@Composable
fun DatosBancarios(
    label: String,
    valor: String,
    copiable: Boolean = false,
    context: Context? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(valor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        if (copiable && context != null) {
            TextButton(
                onClick = {
                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText(label, valor))
                    Toast.makeText(context, "$label copiado", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("📋 Copiar", fontSize = 12.sp, color = Color(0xFF1976D2))
            }
        }
    }
}

@Composable
fun DetalleMercadoPago(
    total: Double,
    pedidoId: String,
    context: Context
) {
    // Este bloque se activará cuando tengas el Access Token
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF009EE3).copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "💳 Pago con MercadoPago",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF009EE3)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Total: $$total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (PagoConfig.MERCADOPAGO_HABILITADO) {
                Button(
                    onClick = {
                        // Aquí irá la integración con MercadoPago
                        // Se activará cuando tengas el Access Token
                        val url = "https://www.mercadopago.com.mx/checkout/v1/redirect?pref_id=TU_PREFERENCE_ID"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009EE3)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pagar con MercadoPago", color = Color.White)
                }
            } else {
                Surface(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "🔒 Próximamente disponible\nEsta opción estará activa muy pronto",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}