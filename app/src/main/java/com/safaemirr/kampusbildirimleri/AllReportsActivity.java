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
 * Bu activity tüm kullanıcıların paylaşılan bildirileri
 * görmesini sağlar. Bildiriler arama ve durum filtresine göre
 * listelenebilir.
 */
public class AllReportsActivity extends AppCompatActivity {

    // Bildirimlerin gösterildiği RecyclerView
    RecyclerView recyclerView;
    ReportAdapter adapter;

    // Arama alanı
    EditText etSearch;

    // Filtre menüsü bileşenleri
    LinearLayout layoutFilterHeader, layoutFilterOptions;
    TextView txtFilterSelected;
    ImageView imgFilterArrow;

    // Filtre seçenekleri
    TextView optionAll, optionOpen, optionPending, optionSolved;

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
        setContentView(R.layout.activity_all_reports);

        // Veritabanı bağlantısı
        db = new DatabaseHelper(this);

        // RecyclerView ayarları
        recyclerView = findViewById(R.id.recyclerPublicReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Arama ve filtre bileşenleri
        etSearch            = findViewById(R.id.etSearchPublic);
        layoutFilterHeader  = findViewById(R.id.layoutFilterHeader);
        layoutFilterOptions = findViewById(R.id.layoutFilterOptions);
        txtFilterSelected   = findViewById(R.id.txtFilterSelected);
        imgFilterArrow      = findViewById(R.id.imgFilterArrow);

        // Filtre seçenekleri
        optionAll     = findViewById(R.id.optionAll);
        optionOpen    = findViewById(R.id.optionOpen);
        optionPending = findViewById(R.id.optionPending);
        optionSolved  = findViewById(R.id.optionSolved);

        // Geri butonu activity’i kapatır
        findViewById(R.id.btnBackAllReports).setOnClickListener(v -> finish());

        // Veriler ilk kez yüklenir
        loadData();

        // Arama ve filtre sistemleri kurulur
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
        recyclerView.setAdapter(adapter);
    }

    // Arama alanına yazıldıkça listeyi filtreler
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activeSearch = s.toString().trim();
                applyFilters();
            }
        });
    }

    // Filtre açılır menüsünün tıklama işlemleri
    private void setupFilter() {

        // Filtre başlığına tıklanınca menü açılır veya kapanır
        layoutFilterHeader.setOnClickListener(v -> toggleFilterMenu());

        // Filtre seçenekleri
        optionAll.setOnClickListener(v -> setFilter("Tümü"));
        optionOpen.setOnClickListener(v -> setFilter("Açık"));
        optionPending.setOnClickListener(v -> setFilter("İnceleniyor"));
        optionSolved.setOnClickListener(v -> setFilter("Çözüldü"));
    }

    // Filtre menüsünü görünür/gizli yapar
    private void toggleFilterMenu() {
        if (layoutFilterOptions.getVisibility() == View.VISIBLE) {
            layoutFilterOptions.setVisibility(View.GONE);
            imgFilterArrow.setRotation(0);
        } else {
            layoutFilterOptions.setVisibility(View.VISIBLE);
            imgFilterArrow.setRotation(180);
        }
    }

    // Seçilen filtreyi aktif hale getirir
    private void setFilter(String filter) {
        activeFilter = filter;
        txtFilterSelected.setText(filter);

        layoutFilterOptions.setVisibility(View.GONE);
        imgFilterArrow.setRotation(0);

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

            boolean statusOK =
                    activeFilter.equals("Tümü") ||
                            status.equalsIgnoreCase(activeFilter);

            boolean searchOK =
                    title.toLowerCase().contains(search) ||
                            desc.toLowerCase().contains(search);

            if (statusOK && searchOK) {
                filteredList.add(r);
            }
        }

        // Filtrelenmiş liste adapter’a gönderilir
        adapter.updateData(filteredList);
    }
}
