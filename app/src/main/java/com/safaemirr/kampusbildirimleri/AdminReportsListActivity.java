package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/*
 * Bu activity admin kullanıcıların tüm bildirileri
 * listelemesini, aramasını ve durumlarına göre filtrelemesini sağlar.
 */
public class AdminReportsListActivity extends AppCompatActivity {

    // Bildirimlerin listelendiği RecyclerView
    RecyclerView recyclerAdminReports;
    ReportAdapter adapter;

    // Arama alanı
    EditText etSearchAdmin;

    // Filtre menüsü bileşenleri
    LinearLayout layoutFilterHeaderAdmin, layoutFilterOptionsAdmin;
    TextView txtFilterSelectedAdmin;
    ImageView imgFilterArrowAdmin;

    // Filtre seçenekleri
    TextView optionAllAdmin, optionOpenAdmin, optionPendingAdmin, optionSolvedAdmin;

    // Veritabanı yardımcı sınıfı
    DatabaseHelper db;

    // Tüm raporların tutulduğu ana liste
    ArrayList<Report> fullList = new ArrayList<>();

    // Aktif filtre ve arama metni
    String activeFilter = "Tümü";
    String activeSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports_list);

        // Veritabanı bağlantısı
        db = new DatabaseHelper(this);

        // RecyclerView ayarları
        recyclerAdminReports = findViewById(R.id.recyclerAdminReports);
        recyclerAdminReports.setLayoutManager(new LinearLayoutManager(this));

        // Arama alanı
        etSearchAdmin = findViewById(R.id.etSearchAdmin);

        // Filtre menüsü bileşenleri
        layoutFilterHeaderAdmin  = findViewById(R.id.layoutFilterHeaderAdmin);
        layoutFilterOptionsAdmin = findViewById(R.id.layoutFilterOptionsAdmin);
        txtFilterSelectedAdmin   = findViewById(R.id.txtFilterSelectedAdmin);
        imgFilterArrowAdmin      = findViewById(R.id.imgFilterArrowAdmin);

        // Filtre seçenekleri
        optionAllAdmin     = findViewById(R.id.optionAllAdmin);
        optionOpenAdmin    = findViewById(R.id.optionOpenAdmin);
        optionPendingAdmin = findViewById(R.id.optionPendingAdmin);
        optionSolvedAdmin  = findViewById(R.id.optionSolvedAdmin);

        // Geri butonu activity’i kapatır
        findViewById(R.id.btnBackAdminReports).setOnClickListener(v -> finish());

        // Veriler yüklenir
        loadData();

        // Arama ve filtre ayarları yapılır
        setupSearch();
        setupFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        applyFilters();
    }

    // Veritabanından tüm raporları çeker ve RecyclerView’a bağlar
    private void loadData() {
        fullList = db.getAllReports();
        if (fullList == null) fullList = new ArrayList<>();

        adapter = new ReportAdapter(this, fullList);
        recyclerAdminReports.setAdapter(adapter);
    }

    // Arama alanına yazıldıkça filtreleme yapılmasını sağlar
    private void setupSearch() {
        etSearchAdmin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activeSearch = s.toString().trim();
                applyFilters();
            }
        });
    }

    // Filtre menüsünün tıklama işlemleri tanımlanır
    private void setupFilter() {

        // Filtre başlığına tıklanınca menü açılır/kapanır
        layoutFilterHeaderAdmin.setOnClickListener(v -> toggleFilterMenu());

        // Filtre seçenekleri
        optionAllAdmin.setOnClickListener(v -> setFilter("Tümü"));
        optionOpenAdmin.setOnClickListener(v -> setFilter("Açık"));
        optionPendingAdmin.setOnClickListener(v -> setFilter("İnceleniyor"));
        optionSolvedAdmin.setOnClickListener(v -> setFilter("Çözüldü"));
    }

    // Filtre menüsünü açıp kapatır
    private void toggleFilterMenu() {
        if (layoutFilterOptionsAdmin.getVisibility() == View.VISIBLE) {
            layoutFilterOptionsAdmin.setVisibility(View.GONE);
            imgFilterArrowAdmin.setRotation(0);
        } else {
            layoutFilterOptionsAdmin.setVisibility(View.VISIBLE);
            imgFilterArrowAdmin.setRotation(180);
        }
    }

    // Seçilen filtreyi aktif hale getirir
    private void setFilter(String filter) {
        activeFilter = filter;
        txtFilterSelectedAdmin.setText(filter);

        layoutFilterOptionsAdmin.setVisibility(View.GONE);
        imgFilterArrowAdmin.setRotation(0);

        applyFilters();
    }

    // Arama metni ve durum filtresini birlikte uygular
    private void applyFilters() {

        ArrayList<Report> filteredList = new ArrayList<>();
        String search = activeSearch.toLowerCase();

        for (Report r : fullList) {

            String status = r.getStatus() == null ? "" : r.getStatus();
            String title  = r.getTitle() == null ? "" : r.getTitle();
            String desc   = r.getDescription() == null ? "" : r.getDescription();

            // Durum filtresi kontrolü
            boolean statusOK =
                    activeFilter.equals("Tümü") ||
                            status.equalsIgnoreCase(activeFilter);

            // Arama filtresi kontrolü
            boolean searchOK =
                    title.toLowerCase().contains(search) ||
                            desc.toLowerCase().contains(search);

            if (statusOK && searchOK) {
                filteredList.add(r);
            }
        }

        // Adapter güncellenir
        adapter.updateData(filteredList);
    }
}
