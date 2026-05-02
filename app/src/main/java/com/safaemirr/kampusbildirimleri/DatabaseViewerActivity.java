package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class DatabaseViewerActivity extends AppCompatActivity {

    Button btnUsers, btnAnnouncements, btnReports;
    ListView listData;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        db = new DatabaseHelper(this);

        btnUsers = findViewById(R.id.btnUsers);
        btnAnnouncements = findViewById(R.id.btnAnnouncements);
        btnReports = findViewById(R.id.btnReports);
        listData = findViewById(R.id.listData);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnUsers.setOnClickListener(v -> loadUsers());
        btnAnnouncements.setOnClickListener(v -> loadAnnouncements());
        btnReports.setOnClickListener(v -> loadReports());
    }

    private void loadUsers() {
        ArrayList<String> data = new ArrayList<>();
        for (UserModel u : db.getAllUsers()) {
            data.add(u.getName() + " (" + u.getEmail() + ") | Role: " + u.getRole());
        }
        listData.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
    }

    private void loadAnnouncements() {
        ArrayList<String> data = new ArrayList<>();
        for (Emergency a : db.getAllAnnouncements()) {
            data.add(a.getTitle() + " - " + a.getCategory());
        }
        listData.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
    }

    private void loadReports() {
        ArrayList<String> data = new ArrayList<>();
        for (Report r : db.getAllReports()) {
            data.add("#" + r.getId() + " " + r.getTitle() + " - " + r.getStatus());
        }
        listData.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
    }
}
