package com.safaemirr.kampusbildirimleri;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/*
 * NotificationHelper
 * - Uygulama içi bildirimleri tek noktadan yönetir
 * - Acil durumlar ve bildirim güncellemeleri için kullanılır
 */
public class NotificationHelper {

    // Bildirim kanalı ID'si (Android 8+ için zorunlu)
    private static final String CHANNEL_ID = "report_updates";

    /*
     * Bildirim gönderme metodu
     * @param context Uygulama context'i
     * @param title   Bildirim başlığı
     * @param message Bildirim içeriği
     */
    public static void send(Context context, String title, String message) {

        // Sistem bildirim servisi alınır
        NotificationManager nm =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0 (Oreo) ve üzeri için notification channel oluşturulur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Bildirim Güncellemeleri",
                            NotificationManager.IMPORTANCE_HIGH
                    );
            nm.createNotificationChannel(channel);
        }

        // Bildirim tasarımı ve ayarları
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher) // Uygulama ikonu
                        .setContentTitle(title)             // Bildirim başlığı
                        .setContentText(message)            // Bildirim mesajı
                        .setAutoCancel(true)                // Tıklanınca otomatik kapanır
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Bildirim gönderilir (unique id için zaman damgası kullanılır)
        nm.notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
    }
}
