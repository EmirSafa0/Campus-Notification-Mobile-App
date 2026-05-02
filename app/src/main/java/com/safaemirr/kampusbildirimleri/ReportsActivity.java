package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/*
 * Bu activity oturum açmış kullanıcının kendi oluşturduğu
 * bildirileri listelemesini sağlar. Kullanıcı arama yapabilir,
 * durum filtresi uygulayabilir ve yeni bildirim ekleyebilir.
 */
public class ReportsActivity extends AppCompatActivity {

    // Kullanıcıya ait bildirilerin listelendiği RecyclerView
    RecyclerView recyclerReports;

    // Arama alanı
    EditText etSearch;

    // Filtre menüsü bileşenleri
    LinearLayout layoutFilterHeader, layoutFilterOptions;
    TextView txtFilterSelected;
    ImageView imgFilterArrow;

    // Filtre seçenekleri
    TextView optionAll, optionOpen, optionPending, optionSolved;

    // Yeni bildirim ekleme butonu
    FloatingActionButton fabAdd;

    // Adapter ve veritabanı yardımcı sınıfı
    ReportAdapter adapter;
    DatabaseHelper db;

    // Kullanıcıya ait tüm raporlar
    ArrayList<Report> reports = new ArrayList<>();

    // Oturumdaki kullanıcının email bilgisi
    String loggedEmail;

    // Aktif filtre ve arama metni
    String activeFilter = "Tümü";
    String activeSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Veritabanı bağlantısı ve kullanıcı bilgisi
        db = new DatabaseHelper(this);
        loggedEmail = SessionManager.getUserEmail(this);

        // RecyclerView ayarları
        recyclerReports = findViewById(R.id.recyclerReports);
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));

        // Arama alanı
        etSearch = findViewById(R.id.etReportSearch);

        // Filtre menüsü bileşenleri
        layoutFilterHeader  = findViewById(R.id.layoutFilterHeader);
        layoutFilterOptions = findViewById(R.id.layoutFilterOptions);
        txtFilterSelected   = findViewById(R.id.txtFilterSelected);
        imgFilterArrow      = findViewById(R.id.imgFilterArrow);

        // Filtre seçenekleri
        optionAll     = findViewById(R.id.optionAll);
        optionOpen    = findViewById(R.id.optionOpen);
        optionPending = findViewById(R.id.optionPending);
        optionSolved  = findViewById(R.id.optionSolved);

        // Yeni bildirim ekleme butonu
        fabAdd = findViewById(R.id.fabAddReport);

        // Geri butonu activity’i kapatır
        findViewById(R.id.btnBackReports).setOnClickListener(v -> finish());

        // Veriler yüklenir ve arama/filtre sistemleri kurulur
        loadReports();
        setupSearch();
        setupFilterMenu();
        setupAddButton();

        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
        applyFilters();
    }

    // Arama alanına yazıldıkça listeyi filtreler
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activeSearch = s.toString().trim();
                applyFilters();
            }
        });
    }

    // Açılır filtre menüsünün tıklama işlemleri
    private void setupFilterMenu() {

        // Filtre başlığına tıklanınca menü açılır veya kapanır
        layoutFilterHeader.setOnClickListener(v -> toggleFilter());

        // Filtre seçenekleri
        optionAll.setOnClickListener(v -> chooseFilter("Tümü"));
        optionOpen.setOnClickListener(v -> chooseFilter("Açık"));
        optionPending.setOnClickListener(v -> chooseFilter("İnceleniyor"));
        optionSolved.setOnClickListener(v -> chooseFilter("Çözüldü"));
    }

    // Filtre menüsünü görünür veya gizli hale getirir
    private void toggleFilter() {
        if (layoutFilterOptions.getVisibility() == View.VISIBLE) {
            layoutFilterOptions.setVisibility(View.GONE);
            imgFilterArrow.setRotation(0);
        } else {
            layoutFilterOptions.setVisibility(View.VISIBLE);
            imgFilterArrow.setRotation(180);
        }
    }

    // Seçilen filtreyi aktif hale getirir
    private void chooseFilter(String f) {
        activeFilter = f;
        txtFilterSelected.setText(f);
        layoutFilterOptions.setVisibility(View.GONE);
        imgFilterArrow.setRotation(0);
        applyFilters();
    }

    // Kullanıcıya ait raporları veritabanından çeker
    private void loadReports() {
        reports = db.getReportsByUser(loggedEmail);
        if (reports == null) reports = new ArrayList<>();

        adapter = new ReportAdapter(this, reports);
        recyclerReports.setAdapter(adapter);
    }

    // Arama metni ve durum filtresini birlikte uygular
    private void applyFilters() {

        ArrayList<Report> filtered = new ArrayList<>();

        for (Report r : reports) {

            boolean statusMatch =
                    activeFilter.equals("Tümü") ||
                            r.getStatus().equalsIgnoreCase(activeFilter);

            boolean searchMatch =
                    r.getTitle().toLowerCase().contains(activeSearch.toLowerCase()) ||
                            r.getDescription().toLowerCase().contains(activeSearch.toLowerCase());

            if (statusMatch && searchMatch) {
                filtered.add(r);
            }
        }

        // Filtrelenmiş liste adapter’a gönderilir
        adapter.updateData(filtered);
    }

    // Yeni bildirim ekleme ekranını açar
    private void setupAddButton() {
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(ReportsActivity.this, AddReportActivity.class))
        );
    }
}
