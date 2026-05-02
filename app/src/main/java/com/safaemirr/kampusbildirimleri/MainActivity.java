package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

// Bildirim izni için gerekli importlar
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // DATABASE

    DatabaseHelper db; // SQLite işlemleri için helper

    // TOP BAR BİLEŞENLERİ

    TextView tvTitle;         // Uygulama başlığı
    TextView tvUserShortInfo; // Kullanıcı karşılama metni

    // ACİL DURUM BUTONU

    LinearLayout cardEmergency;      // Acil durum kartı
    TextView txtEmergencyTitle;      // Acil durum başlığı
    TextView txtEmergencyDesc;       // Acil durum açıklaması


    // ANA SAYFA BUTONLARI

    LinearLayout
            cardCampusMap,     // Kampüs haritası
            cardPublicReports, // Tüm bildirimler
            cardMyReports,     // Kullanıcının bildirimleri
            cardProfile,       // Profil
            cardAdminPanel,    // Admin paneli (sadece ADMIN)
            btnLogout;         // Çıkış butonu

    // SESSION BİLGİLERİ

    String loggedEmail; // Giriş yapan kullanıcının emaili
    String loggedRole;  // Kullanıcı rolü (ADMIN / USER)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DATABASE INIT

        db = new DatabaseHelper(this);

        // SESSION KONTROLÜ

        // Kullanıcı giriş yapmamışsa Login ekranına yönlendir.

        loggedEmail = SessionManager.getUserEmail(this);
        if (loggedEmail == null) {
            goTo(LoginActivity.class, true);
            return;
        }

        // Kullanıcı rolü (ADMIN / USER)
        loggedRole = db.getUserRole(loggedEmail);

        tvTitle           = findViewById(R.id.tvTitle);
        tvUserShortInfo   = findViewById(R.id.tvUserShortInfo);

        cardEmergency     = findViewById(R.id.cardEmergency);
        txtEmergencyTitle = findViewById(R.id.txtEmergencyTitle);
        txtEmergencyDesc  = findViewById(R.id.txtEmergencyDesc);

        cardCampusMap     = findViewById(R.id.cardCampusMap);
        cardPublicReports = findViewById(R.id.cardPublicReports);
        cardMyReports     = findViewById(R.id.cardMyReports);
        cardProfile       = findViewById(R.id.cardProfile);
        cardAdminPanel    = findViewById(R.id.cardAdminPanel);
        btnLogout         = findViewById(R.id.btnLogout);

        // KULLANICI BİLGİSİ GÖSTER

        String name = db.getUserName(loggedEmail);
        tvUserShortInfo.setText(
                "Hoş geldin " + (name != null ? name : "Kullanıcı")
        );

 
        // ACİL DURUM KARTINI HAZIRLA

        setupEmergencyCard();

        // Acil durum detay ekranı
        cardEmergency.setOnClickListener(v ->
                goTo(EmergencyActivity.class, false)
        );

        // Kampüs haritası
        cardCampusMap.setOnClickListener(v ->
                goTo(MapActivity.class, false)
        );

        // Tüm bildirimler
        cardPublicReports.setOnClickListener(v ->
                goTo(AllReportsActivity.class, false)
        );

        // Kullanıcının kendi bildirimleri
        cardMyReports.setOnClickListener(v ->
                goTo(ReportsActivity.class, false)
        );

        // Profil ekranı
        cardProfile.setOnClickListener(v ->
                goTo(ProfileActivity.class, false)
        );

        // Çıkış yap
        btnLogout.setOnClickListener(v -> logout());

        // GİZLİ ADMIN DB VIEWER Sadece Admin hoşgeldin yönetici yazan yere uzun basarsa açılır.

        if ("ADMIN".equalsIgnoreCase(loggedRole)) {
            tvUserShortInfo.setOnLongClickListener(v -> {
                Toast.makeText(
                        this,
                        "Database Viewer açılıyor...",
                        Toast.LENGTH_SHORT
                ).show();
                goTo(DatabaseViewerActivity.class, false);
                return true;
            });
        }

        // Admin panel butonu. Ana sayfada sadece Admin görür.
        
        if ("ADMIN".equalsIgnoreCase(loggedRole)) {
            cardAdminPanel.setVisibility(View.VISIBLE);
            cardAdminPanel.setOnClickListener(v ->
                    goTo(AdminDashboardActivity.class, false)
            );
        }

        // ANDROID 13+ BİLDİRİM İZNİ

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ana ekrana her dönüşte acil durum sayısını günceller.
        setupEmergencyCard();
    }

    //Aktif acil durum sayısını DB'den çeker. Kart içeriğini ve acil durum olunca kırmızı buton yokken saydam buton olacak şekilde ayarlar.
     
    private void setupEmergencyCard() {

        int emergencyCount = db.getActiveEmergencyCount();

        cardEmergency.setVisibility(View.VISIBLE);

        if (emergencyCount > 0) {
            txtEmergencyTitle.setText(
                    "🚨 " + emergencyCount + " AKTİF ACİL DURUM"
            );
            txtEmergencyDesc.setText(
                    "Detayları görmek için dokun"
            );
            cardEmergency.setAlpha(1f);
        } else {
            txtEmergencyTitle.setText("🚨 Acil Durum");
            txtEmergencyDesc.setText(
                    "Şu anda aktif bir acil durum yok"
            );
            cardEmergency.setAlpha(0.6f);
        }
    }

    private void goTo(Class<?> cls, boolean clearTask) {
        Intent i = new Intent(MainActivity.this, cls);
        if (clearTask) {
            i.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
        }
        startActivity(i);
    }

    // Session temizlenir ve Login ekranına dönülür
    private void logout() {
        SessionManager.logout(this);
        goTo(LoginActivity.class, true);
    }
}
