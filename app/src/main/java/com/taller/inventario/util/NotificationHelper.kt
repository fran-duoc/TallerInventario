package com.taller.inventario.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.taller.inventario.MainActivity

/**
 * Gestor de recursos nativos del sistema: Notificaciones locales de Android.
 * Notifica al operario y jefe de taller cuando el stock de un repuesto entra en estado crítico o agotado.
 */
object NotificationHelper {

    const val CHANNEL_ID = "taller_alertas_stock"
    private const val CHANNEL_NAME = "Alertas de Stock - Taller Mecánico"
    private const val CHANNEL_DESC = "Notificaciones automáticas ante repuestos agotados o bajo umbral mínimo"

    /**
     * Registra el canal de notificaciones en el sistema operativo (requerido para Android 8.0+).
     */
    fun crearCanalNotificaciones(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    /**
     * Emite una notificación del sistema indicando stock crítico o agotado.
     */
    fun emitirAlertaStock(
        context: Context,
        nombreProducto: String,
        codigo: String,
        cantidadActual: Int,
        reposicionSugerida: Int
    ) {
        crearCanalNotificaciones(context)

        // Verificación de permiso en runtime para Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso no concedido aún
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val esAgotado = cantidadActual == 0
        val titulo = if (esAgotado) {
            "🚨 ¡REPUESTO AGOTADO! Taller Puente Alto"
        } else {
            "⚠️ ATENCIÓN: Repuesto con Bajo Stock"
        }

        val contenido = if (esAgotado) {
            "El repuesto '$nombreProducto' ($codigo) no tiene existencias. Reponer urgente $reposicionSugerida unidades."
        } else {
            "'$nombreProducto' ($codigo) quedan solo $cantidadActual un. Sugerido reponer: $reposicionSugerida un."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contenido))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            // Genera ID único usando el hash del código para no colisionar
            val notificationId = codigo.hashCode()
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Manejo seguro ante restricciones de seguridad del dispositivo
        }
    }
}
