package com.example.online_health_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.online_health_app.data.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


const val channelId = "FirebaseMessagingService"

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.

            // Create a store instance.
            val store = FirebaseFirestore.getInstance()

            // Create a Query object
            val notificationRef = store.collection("notifications")

            val sharedPreferences =
                this.getSharedPreferences("user", Context.MODE_PRIVATE)

            val uuid = sharedPreferences.getString("uuid", "")

            // Save the notification with the generated UUID
            notificationRef.document(uuid.toString())
                .set(
                    Notification(
                        remoteMessage.notification?.title.toString(),
                        remoteMessage.notification?.body.toString(),
                        SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date()).toString(),
                        R.drawable.bell_45
                    )
                )
                .addOnSuccessListener {
                    generateNotification(
                        remoteMessage.notification?.title.toString(),
                        remoteMessage.notification?.body.toString()
                    )
                }
                .addOnFailureListener { _ ->

                }


        }
    }

    private fun getRemoteView(title: String, content: String): RemoteViews {
        val remoteView = RemoteViews("com.example.online_health_app", R.layout.push_notification)

        remoteView.setTextViewText(R.id.notificationTitle, title)
        remoteView.setTextViewText(R.id.notificationContent, content)

        return remoteView
    }

    private fun generateNotification(title: String, content: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingActivity = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.bell_45)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingActivity)

        builder = builder.setContent(getRemoteView(title, content))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            channelId, "web_app",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(
            notificationChannel
        )

        notificationManager.notify(0, builder.build())

    }
}