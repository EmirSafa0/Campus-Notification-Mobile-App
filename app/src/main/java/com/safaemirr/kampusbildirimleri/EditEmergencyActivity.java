package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditEmergencyActivity extends AppCompatActivity {

    EditText editTitle, editDescription;
    TextView btnSave, btnCancel;

    DatabaseHelper db;
    int announcementId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_emergency);

        db = new DatabaseHelper(this);

        // ===== UI =====
        editTitle       = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);

        btnSave   = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // ===== DATA =====
        announcementId = getIntent().getIntExtra("id", -1);
        String oldTitle = getIntent().getStringExtra("title");
        String oldDesc  = getIntent().getStringExtra("description");

        if (announcementId == -1) {
            Toast.makeText(this,
                    "Acil durum bulunamadı!",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTitle.setText(oldTitle != null ? oldTitle : "");
        editDescription.setText(oldDesc != null ? oldDesc : "");

        // ===== ACTIONS =====
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    // =====================================================
    // 💾 KAYDET (ACİL DURUM)
    // =====================================================
    private void saveChanges() {

        String newTitle = editTitle.getText().toString().trim();
        String newDesc  = editDescription.getText().toString().trim();

        if (newTitle.isEmpty()) {
            editTitle.setError("Başlık boş olamaz");
            return;
        }

        if (newDesc.isEmpty()) {
            editDescription.setError("Açıklama boş olamaz");
            return;
        }

        // 🚨 SABİT KATEGORİ
        String category = "Acil Durum";

        boolean updated = db.editAnnouncement(
                announcementId,
                newTitle,
                category,
                newDesc
        );

        if (updated) {
            Toast.makeText(this,
                    "Acil durum güncellendi.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this,
                    "Güncelleme başarısız!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
