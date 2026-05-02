package com.safaemirr.kampusbildirimleri;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // SharedPreferences dosya adı
    private static final String PREF_NAME = "UserSession";

    // Kaydedilecek anahtarlar
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE  = "role";

    // Kullanıcı giriş yaptığında email ve rol bilgisini kaydeder
    public static void saveUser(Context context, String email, String role) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putString(KEY_EMAIL, email) // kullanıcı emaili
                .putString(KEY_ROLE, role)   // kullanıcı rolü (ADMIN / USER)
                .apply();                    // asenkron kaydet
    }

    // Kullanıcı daha önce giriş yapmış mı kontrol eder
    public static boolean isLoggedIn(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Email varsa kullanıcı giriş yapmış kabul edilir
        return prefs.contains(KEY_EMAIL);
    }

    // Kaydedilen kullanıcı emailini döndürür
    public static String getUserEmail(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Email yoksa null döner
        return prefs.getString(KEY_EMAIL, null);
    }

    // Kaydedilen kullanıcı rolünü döndürür
    public static String getUserRole(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Rol yoksa varsayılan olarak USER kabul edilir
        return prefs.getString(KEY_ROLE, "USER");
    }

    // Çıkış yapıldığında session tamamen temizlenir
    public static void logout(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Tüm kayıtları sil
        prefs.edit().clear().apply();
    }
}
