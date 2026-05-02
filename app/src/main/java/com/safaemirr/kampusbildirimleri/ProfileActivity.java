package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    // Kullanıcı bilgileri
    TextView txtName, txtEmail, txtRole, txtDepartment;

    // Takip edilen bildirim yoksa gösterilen yazı
    TextView txtNoFollowed;

    // Üst bar geri butonu
    TextView btnBackProfile;

    // Çıkış yap butonu
    Button btnLogout;

    // Takip edilen bildirimlerin listesi
    RecyclerView recyclerFollowedReports;

    // Veritabanı ve liste verileri
    DatabaseHelper db;
    ArrayList<Report> followedReports;
    ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Database helper
        db = new DatabaseHelper(this);

        // View bağlama işlemleri
        bindViews();

        // Kullanıcı profil bilgilerini yükle
        loadProfileInfo();

        // Takip edilen bildirimleri yükle
        loadFollowedReports();

        // Geri butonu önceki ekrana dön
        btnBackProfile.setOnClickListener(v -> finish());

        // Çıkış yap session temizlenir ve login ekranına gidilir
        btnLogout.setOnClickListener(v -> {
            SessionManager.logout(this);
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    // View’ların XML ile eşleştirilmesi
    private void bindViews() {

        txtName       = findViewById(R.id.txtName);
        txtEmail      = findViewById(R.id.txtEmail);
        txtRole       = findViewById(R.id.txtRole);
        txtDepartment = findViewById(R.id.txtDepartment);

        txtNoFollowed  = findViewById(R.id.txtNoFollowed);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        btnLogout      = findViewById(R.id.btnLogout);

        recyclerFollowedReports = findViewById(R.id.recyclerFollowedReports);
        recyclerFollowedReports.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    // Kullanıcının profil bilgilerini veritabanından çeker
    private void loadProfileInfo() {

        String email = SessionManager.getUserEmail(this);
        if (email == null) return;

        UserModel user = db.getUserByEmail(email);
        if (user == null) return;

        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtRole.setText("Rol: " + user.getRole());
        txtDepartment.setText("Birim: " + user.getUnit());
    }

    // Kullanıcının takip ettiği bildirimleri yükler
    private void loadFollowedReports() {

        String email = SessionManager.getUserEmail(this);
        if (email == null) return;

        followedReports = db.getFollowedReports(email);

        // Takip edilen bildirim yoksa bilgilendirme yazısı gösterilir
        if (followedReports == null || followedReports.isEmpty()) {
            txtNoFollowed.setVisibility(View.VISIBLE);
            recyclerFollowedReports.setVisibility(View.GONE);
        } 
        // Takip edilen bildirim varsa RecyclerView doldurulur
        else {
            txtNoFollowed.setVisibility(View.GONE);
            recyclerFollowedReports.setVisibility(View.VISIBLE);

            adapter = new ReportAdapter(this, followedReports);
            recyclerFollowedReports.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Detaydan geri dönüldüğünde liste güncellenir
        loadFollowedReports();
    }
}
