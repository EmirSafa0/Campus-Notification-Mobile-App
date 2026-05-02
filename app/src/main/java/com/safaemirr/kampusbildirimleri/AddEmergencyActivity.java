package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEmergencyActivity extends AppCompatActivity {

    // UI
    EditText etTitle, etDescription;
    Button btnPublish;
    TextView btnBack;

    // DB
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency);

        // ===== DB =====
        db = new DatabaseHelper(this);

        // ===== BIND =====
        etTitle       = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnPublish    = findViewById(R.id.btnPublish);
        btnBack       = findViewById(R.id.btnBackAdd);

        btnBack.setOnClickListener(v -> finish());

        // ===== ADMIN KONTROL =====
        if (!isAdmin()) {
            btnPublish.setEnabled(false);
            btnPublish.setAlpha(0.4f);

            Toast.makeText(
                    this,
                    "Bu ekran sadece ADMIN tarafından kullanılabilir!",
                    Toast.LENGTH_LONG
            ).show();
        }

        // ===== YAYINLA =====
        btnPublish.setOnClickListener(v -> publishEmergency());
    }

    // =====================================================
    // 🔐 ADMIN Mİ?
    // =====================================================
    private boolean isAdmin() {
        SharedPreferences prefs =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        String email = prefs.getString("email", "");
        String role  = db.getUserRole(email);

        return "ADMIN".equalsIgnoreCase(role);
    }

    // =====================================================
    // 🚨 ACİL DURUM YAYINLA
    // =====================================================
    private void publishEmergency() {

        if (!isAdmin()) {
            Toast.makeText(
                    this,
                    "Sadece admin acil durum yayınlayabilir!",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String desc  = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Başlık boş olamaz");
            return;
        }

        if (desc.isEmpty()) {
            etDescription.setError("Açıklama boş olamaz");
            return;
        }

        // 🚨 SABİT KATEGORİ
        String category = "Acil Durum";

        String date = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        boolean success = db.addAnnouncement(
                title,
                desc,
                category,
                date
        );

        if (success) {
            Toast.makeText(
                    this,
                    "🚨 Acil durum bildirimi yayınlandı!",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        } else {
            Toast.makeText(
                    this,
                    "Acil durum eklenirken hata oluştu!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
