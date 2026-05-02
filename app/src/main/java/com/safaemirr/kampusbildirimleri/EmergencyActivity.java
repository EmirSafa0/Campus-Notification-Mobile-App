package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EmergencyActivity extends AppCompatActivity {

    // UI
    RecyclerView recyclerView;
    TextView btnBack, txtEmpty;
    FloatingActionButton fabAdd;

    // DATA
    DatabaseHelper db;
    ArrayList<Emergency> emergencyList;
    EmergencyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        db = new DatabaseHelper(this);

        // ===== BIND =====
        recyclerView = findViewById(R.id.recyclerEmergency);
        btnBack      = findViewById(R.id.btnBackEmergency);
        fabAdd       = findViewById(R.id.fabAddEmergency);
        txtEmpty     = findViewById(R.id.txtEmptyEmergency);

        // ===== BACK =====
        btnBack.setOnClickListener(v -> finish());

        // ===== LIST =====
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ===== LOAD =====
        loadEmergencies();

        // ===== ADMIN KONTROL (FAB) =====
        if (isAdmin()) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v ->
                    startActivity(
                            new Intent(EmergencyActivity.this, AddEmergencyActivity.class)
                    )
            );
        } else {
            fabAdd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmergencies(); // Detaydan / ekrandan dönünce yenile
    }

    // =====================================================
    // 🚨 SADECE AKTİF ACİL DURUMLAR
    // =====================================================
    private void loadEmergencies() {

        emergencyList = db.getActiveEmergencies();

        if (emergencyList == null || emergencyList.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new EmergencyAdapter(this, emergencyList);
            recyclerView.setAdapter(adapter);
        }
    }

    // =====================================================
    // 🔐 ADMIN Mİ?
    // =====================================================
    private boolean isAdmin() {
        String email = SessionManager.getUserEmail(this);
        if (email == null) return false;

        String role = db.getUserRole(email);
        return "ADMIN".equalsIgnoreCase(role);
    }
}
