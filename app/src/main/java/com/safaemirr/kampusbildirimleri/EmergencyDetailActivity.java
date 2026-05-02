package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyDetailActivity extends AppCompatActivity {

    // UI
    TextView btnBack;
    TextView txtTitle, txtDesc, txtDate;
    View layoutAdminButtons;
    Button btnEdit, btnClose;

    // DB
    DatabaseHelper db;

    int announcementId = -1;
    Emergency emergency = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_detail);

        db = new DatabaseHelper(this);

        // ===== BIND =====
        btnBack = findViewById(R.id.btnBackEmergencyDetail);

        txtTitle = findViewById(R.id.txtEmergencyTitle);
        txtDesc  = findViewById(R.id.txtEmergencyDesc);
        txtDate  = findViewById(R.id.txtEmergencyDate);

        layoutAdminButtons = findViewById(R.id.layoutEmergencyAdminButtons);
        btnEdit  = findViewById(R.id.btnEmergencyEdit);
        btnClose = findViewById(R.id.btnEmergencyClose);

        btnBack.setOnClickListener(v -> finish());

        // ===== ID =====
        announcementId = getIntent().getIntExtra("id", -1);
        if (announcementId == -1) {
            Toast.makeText(this, "Acil durum bulunamadı!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadFromDatabase();

        // ===== ADMIN KONTROL =====
        if (isAdmin()) {
            layoutAdminButtons.setVisibility(View.VISIBLE);
            setupAdminButtons();
        } else {
            layoutAdminButtons.setVisibility(View.GONE);
        }
    }


    private void loadFromDatabase() {
        emergency = db.getAnnouncementById(announcementId);

        if (emergency == null) {
            Toast.makeText(this, "Acil durum silinmiş veya kapatılmış!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtTitle.setText(emergency.getTitle());
        txtDesc.setText(emergency.getDescription());
        txtDate.setText(emergency.getDate());
    }

    // =====================================================
    // 🔐 ADMIN Mİ?
    // =====================================================
    private boolean isAdmin() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        if (email == null || email.isEmpty()) return false;

        String role = db.getUserRole(email);
        return "ADMIN".equalsIgnoreCase(role);
    }

    // =====================================================
    // 🧠 ADMIN BUTONLARI
    // =====================================================
    private void setupAdminButtons() {

        // ✏️ DÜZENLE
        btnEdit.setOnClickListener(v -> {
            if (emergency == null) return;

            Intent i = new Intent(this, EditEmergencyActivity.class);
            i.putExtra("id", emergency.getId());
            i.putExtra("title", emergency.getTitle());
            i.putExtra("description", emergency.getDescription());
            startActivity(i);
        });

        // 🚨 ACİL DURUMU KAPAT (SİLME YOK!)
        btnClose.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Acil Durumu Kapat")
                    .setMessage("Bu acil durumu kapatmak istiyor musunuz?\n(Kullanıcılara artık gösterilmeyecek)")
                    .setNegativeButton("İptal", (d, w) -> d.dismiss())
                    .setPositiveButton("Evet, Kapat", (d, w) -> {

                        boolean ok = db.closeAnnouncement(announcementId);

                        if (ok) {
                            Toast.makeText(
                                    this,
                                    "Acil durum başarıyla kapatıldı.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            finish();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Acil durum kapatılamadı!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFromDatabase();
    }
}
