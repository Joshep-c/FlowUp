package com.app.flowup.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.app.flowup.MainActivity
import com.app.flowup.R
import com.app.flowup.data.local.ActivityEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Gestiona canales, creación y programación de notificaciones.

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "flowup_reminders"
        private const val CHANNEL_NAME = "Recordatorios"
    }

    init {
        createNotificationChannel()
    }

    // Crea el canal de notificaciones (Android 8.0+).

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de tus actividades pendientes"
                enableVibration(true)
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    //Muestra una notificación simple.

    @SuppressLint("MissingPermission")
    fun showNotification(activityId: Long, title: String, message: String) {
        if (!hasPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("activity_id", activityId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            activityId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(activityId.toInt(), notification)
        } catch (e: SecurityException) {
            // Permiso denegado
        }
    }

    // Programa un recordatorio para una actividad.

    fun scheduleReminder(activity: ActivityEntity, daysNotice: Int) {
        val reminderTime = activity.dueDate - TimeUnit.DAYS.toMillis(daysNotice.toLong())
        val currentTime = System.currentTimeMillis()

        if (reminderTime <= currentTime) return

        val delay = reminderTime - currentTime

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "activity_id" to activity.id,
                "title" to activity.title,
                "message" to activity.description.ifEmpty { "Tienes una actividad pendiente" }
            ))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_${activity.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Cancela un recordatorio programado.

    fun cancelReminder(activityId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_$activityId")
    }

    // Verifica si tiene permiso de notificaciones (Android 13+).

    fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

// Worker simple que muestra la notificación en el momento programado.

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val activityId = inputData.getLong("activity_id", -1L)
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: ""

        if (activityId == -1L) return Result.failure()

        // Verificar permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.success()
            }
        }

        // Crear notificación
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("activity_id", activityId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            activityId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "flowup_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio: $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.notify(activityId.toInt(), notification)
        } catch (e: SecurityException) {
            // Permiso denegado
        }

        return Result.success()
    }
}

