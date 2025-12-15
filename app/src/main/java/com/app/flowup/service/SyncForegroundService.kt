package com.app.flowup.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.app.flowup.MainActivity
import com.app.flowup.R
import kotlinx.coroutines.*

/**
 * Servicio en primer plano simple para MVP.
 * Mantiene la app sincronizada y monitorea actividades pendientes.
 */
class SyncForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var syncJob: Job? = null

    companion object {
        private const val NOTIFICATION_ID = 1000
        private const val CHANNEL_ID = "flowup_sync_service"
        private const val CHANNEL_NAME = "Sincronización FlowUp"

        /**
         * Inicia el servicio en primer plano.
         */
        fun start(context: Context) {
            val intent = Intent(context, SyncForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * Detiene el servicio.
         */
        fun stop(context: Context) {
            val intent = Intent(context, SyncForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Iniciando..."))
        startSync()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Se reinicia automáticamente si el sistema lo mata
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopSync()
        serviceScope.cancel()
    }

    /**
     * Inicia la sincronización periódica.
     */
    private fun startSync() {
        syncJob = serviceScope.launch {
            var syncCount = 0
            while (isActive) {
                try {
                    syncCount++
                    updateNotification("Sincronizado ($syncCount)")
                    delay(60_000) // Cada minuto
                } catch (e: Exception) {
                    updateNotification("Error en sincronización")
                    delay(120_000) // 2 minutos en caso de error
                }
            }
        }
    }

    /**
     * Detiene la sincronización.
     */
    private fun stopSync() {
        syncJob?.cancel()
        syncJob = null
    }

    /**
     * Crea el canal de notificaciones (Android 8.0+).
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Baja prioridad para no molestar
            ).apply {
                description = "Mantiene FlowUp sincronizado en segundo plano"
                setShowBadge(false)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Crea la notificación del servicio.
     */
    private fun createNotification(message: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FlowUp")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // No se puede deslizar para cerrar
            .setShowWhen(false)
            .build()
    }

    /**
     * Actualiza la notificación del servicio.
     */
    private fun updateNotification(message: String) {
        val notification = createNotification(message)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
}

