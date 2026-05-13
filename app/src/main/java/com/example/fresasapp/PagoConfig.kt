package com.example.fresasapp

object PagoConfig {
    // ⚠️ Cuando tengas el Access Token de MercadoPago reemplaza esta línea
    const val MERCADOPAGO_ACCESS_TOKEN = "TU_ACCESS_TOKEN_AQUI"
    const val MERCADOPAGO_HABILITADO = false // ← cambia a true cuando tengas el token

    // Datos para transferencia SPEI
    // ⚠️ Reemplaza con los datos bancarios de tu esposa
    const val BANCO = "BBVA" // nombre de tu banco
    const val CLABE = "000000000000000000" // tu CLABE interbancaria
    const val TITULAR = "Nombre del titular"
    const val CONCEPTO = "Pedido Nay&Jos"
}