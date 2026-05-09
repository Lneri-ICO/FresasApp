package com.example.fresasapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.fresasapp.data.model.Pedido
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketPdfGenerator {

    fun generarTicket(context: Context, pedido: Pedido): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paintTitulo = Paint().apply {
                color = Color.parseColor("#E91E63")
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val paintSubtitulo = Paint().apply {
                color = Color.parseColor("#880E4F")
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val paintNormal = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                textAlign = Paint.Align.LEFT
            }

            val paintNormalDerecha = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                textAlign = Paint.Align.RIGHT
            }

            val paintGris = Paint().apply {
                color = Color.GRAY
                textSize = 12f
                textAlign = Paint.Align.LEFT
            }

            val paintLinea = Paint().apply {
                color = Color.parseColor("#E91E63")
                strokeWidth = 2f
                style = Paint.Style.STROKE
            }

            val paintLineaGris = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1f
                style = Paint.Style.STROKE
            }

            val paintTotal = Paint().apply {
                color = Color.parseColor("#E91E63")
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
            }

            val paintTotalDerecha = Paint().apply {
                color = Color.parseColor("#E91E63")
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }

            val centerX = 297.5f
            var y = 60f

            // Título
            canvas.drawText("🍓 Nay&Jos", centerX, y, paintTitulo)
            y += 30f
            canvas.drawText("Postres frescos y deliciosos", centerX, y, paintSubtitulo)
            y += 20f

            // Línea separadora
            canvas.drawLine(40f, y, 555f, y, paintLinea)
            y += 25f

            // Info del ticket
            canvas.drawText("TICKET DE COMPRA", centerX, y, paintSubtitulo)
            y += 25f

            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(pedido.fechaCreacion))

            canvas.drawLine(40f, y, 555f, y, paintLineaGris)
            y += 20f

            canvas.drawText("Pedido:", 40f, y, paintGris)
            canvas.drawText("#${pedido.id.take(8).uppercase()}", 555f, y, paintNormalDerecha)
            y += 22f

            canvas.drawText("Fecha:", 40f, y, paintGris)
            canvas.drawText(fecha, 555f, y, paintNormalDerecha)
            y += 22f

            canvas.drawText("Cliente:", 40f, y, paintGris)
            canvas.drawText(pedido.clienteNombre, 555f, y, paintNormalDerecha)
            y += 22f

            canvas.drawText("Entrega:", 40f, y, paintGris)
            canvas.drawText(
                if (pedido.tipoEntrega == "delivery") "Delivery a domicilio" else "Recoger en tienda",
                555f, y, paintNormalDerecha
            )
            y += 22f

            if (pedido.direccion.isNotEmpty()) {
                canvas.drawText("Dirección:", 40f, y, paintGris)
                canvas.drawText(pedido.direccion, 555f, y, paintNormalDerecha)
                y += 22f
            }

            canvas.drawLine(40f, y, 555f, y, paintLineaGris)
            y += 25f

            // Productos
            val paintProductosTitulo = Paint().apply {
                color = Color.BLACK
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("PRODUCTOS", 40f, y, paintProductosTitulo)
            y += 8f
            canvas.drawLine(40f, y, 555f, y, paintLineaGris)
            y += 20f

            pedido.items.forEach { item ->
                canvas.drawText("${item.nombre} x${item.cantidad}", 40f, y, paintNormal)
                canvas.drawText("$${item.precio}", 555f, y, paintNormalDerecha)
                y += 22f
            }

            y += 5f
            canvas.drawLine(40f, y, 555f, y, paintLinea)
            y += 25f

            // Total
            canvas.drawText("TOTAL", 40f, y, paintTotal)
            canvas.drawText("$${pedido.total}", 555f, y, paintTotalDerecha)
            y += 35f

            // Estado
            canvas.drawLine(40f, y, 555f, y, paintLineaGris)
            y += 20f

            val paintEstado = Paint().apply {
                color = Color.parseColor("#388E3C")
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Estado: ${pedido.estado.replaceFirstChar { it.uppercase() }}", centerX, y, paintEstado)
            y += 40f

            // Mensaje de agradecimiento
            val paintGracias = Paint().apply {
                color = Color.parseColor("#E91E63")
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("¡Gracias por tu compra! 🍓", centerX, y, paintGracias)
            y += 22f

            val paintMensaje = Paint().apply {
                color = Color.GRAY
                textSize = 12f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Vuelve pronto a Nay&Jos", centerX, y, paintMensaje)

            document.finishPage(page)

            // Guardar archivo
            val nombreArchivo = "ticket_${pedido.id.take(8)}.pdf"
            val carpeta = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val archivo = File(carpeta, nombreArchivo)

            val outputStream = FileOutputStream(archivo)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()

            archivo
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}