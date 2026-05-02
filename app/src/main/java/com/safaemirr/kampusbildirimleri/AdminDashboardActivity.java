package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdminDashboardActivity extends AppCompatActivity {

    DatabaseHelper db;

    TextView txtTotalReports, txtPendingReports, txtSolvedReports, txtAnnouncementCount, btnBackAdminDashboard;
    LinearLayout cardManageReports, cardManageAnnouncements, cardAddAnnouncement;
    Button btnAdminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = new DatabaseHelper(this);

        // ==== Kullanıcı kontrolü ====
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email == null) {
            goToLogin();
            return;
        }

        String role = db.getUserRole(email);

        if (!"ADMIN".equals(role)) {
            Toast.makeText(this, "Bu sayfaya erişim yetkiniz yok!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ==== UI ====
        txtTotalReports      = findViewById(R.id.txtTotalReports);
        txtPendingReports    = findViewById(R.id.txtPendingReports);
        txtSolvedReports     = findViewById(R.id.txtSolvedReports);
        txtAnnouncementCount = findViewById(R.id.txtAnnouncementCount);

        btnBackAdminDashboard = findViewById(R.id.btnBackAdminDashboard);
        btnAdminLogout        = findViewById(R.id.btnAdminLogout);

        cardManageReports       = findViewById(R.id.cardManageReports);
        cardManageAnnouncements = findViewById(R.id.cardManageAnnouncements);
        cardAddAnnouncement     = findViewById(R.id.cardAddAnnouncement);

        // ==== Sayılar ====
        txtTotalReports.setText(getString(R.string.total_reports, db.countReports()));
        txtPendingReports.setText(getString(R.string.pending_reports, db.countPendingReports()));
        txtSolvedReports.setText(getString(R.string.solved_reports, db.countSolvedReports()));
        txtAnnouncementCount.setText(getString(R.string.total_announcements, db.countAnnouncements()));

        // ==== Navigasyon ====
        cardManageReports.setOnClickListener(v ->
                startActivity(new Intent(this, AdminReportsListActivity.class)));

        cardManageAnnouncements.setOnClickListener(v ->
                startActivity(new Intent(this, EmergencyActivity.class)));

        cardAddAnnouncement.setOnClickListener(v ->
                startActivity(new Intent(this, AddEmergencyActivity.class)));

        btnBackAdminDashboard.setOnClickListener(v -> goToMain());

        btnAdminLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            goToLogin();
        });

        // ===== YENİ ANDROID BACK NAVIGATION =====
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToMain();
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
