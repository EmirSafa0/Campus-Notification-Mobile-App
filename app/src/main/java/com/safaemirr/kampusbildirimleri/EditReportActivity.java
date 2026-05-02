package com.safaemirr.kampusbildirimleri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Bu activity mevcut bir bildirimin düzenlenmesini sağlar.
 * Kullanıcı başlık, açıklama, kategori ve konum bilgilerini güncelleyebilir.
 */
public class EditReportActivity extends AppCompatActivity {

    // Bildirim bilgilerini düzenlemek için kullanılan alanlar
    EditText etTitle, etDescription, etLocation;
    Spinner spCategory;
    Button btnSave;
    TextView btnBack;

    // Veritabanı yardımcı sınıfı ve düzenlenen rapor
    DatabaseHelper db;
    Report report;

    // Seçilen konum bilgileri
    Double selectedLat, selectedLng;

    // Haritadan konum seçimi için request code
    private static final int REQ_PICK_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);

        // Veritabanı bağlantısı
        db = new DatabaseHelper(this);

        // XML bileşenleri Java tarafına bağlanır
        etTitle       = findViewById(R.id.etReportTitle);
        etDescription = findViewById(R.id.etReportDescription);
        etLocation    = findViewById(R.id.etReportLocation);
        spCategory    = findViewById(R.id.spReportCategory);
        btnSave       = findViewById(R.id.btnSaveReport);
        btnBack       = findViewById(R.id.btnBackEdit);

        // Kategori listesi yüklenir
        loadCategories();

        // Düzenlenecek rapor bilgileri yüklenir
        loadReport();

        // Haritadan konum seçme işlemi
        findViewById(R.id.btnPickLocation).setOnClickListener(v -> openMap());

        // Geri butonu activity’i kapatır
        btnBack.setOnClickListener(v -> finish());

        // Kaydet butonu güncelleme işlemini başlatır
        btnSave.setOnClickListener(v -> save());
    }

    // Kategori seçeneklerini spinner’a yükler
    private void loadCategories() {
        String[] categories = {
                "Güvenlik",
                "Kayıp Eşya",
                "Şiddet / Kavga",
                "Hırsızlık",
                "Sağlık",
                "Çevre / Temizlik",
                "Teknik Arıza",
                "Diğer"
        };

        spCategory.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        ));
    }

    // Intent ile gelen report_id’ye göre rapor bilgilerini yükler
    private void loadReport() {
        int id = getIntent().getIntExtra("report_id", -1);
        if (id == -1) {
            finish();
            return;
        }

        report = db.getReportById(id);
        if (report == null) {
            finish();
            return;
        }

        // Mevcut rapor bilgileri ekrana yazdırılır
        etTitle.setText(report.getTitle());
        etDescription.setText(report.getDescription());
        etLocation.setText(
                "Lat: " + report.getLatitude() + " / Lng: " + report.getLongitude()
        );

        // Mevcut konum bilgileri atanır
        selectedLat = report.getLatitude();
        selectedLng = report.getLongitude();

        // Mevcut kategori spinner’da seçilir
        for (int i = 0; i < spCategory.getCount(); i++) {
            if (spCategory.getItemAtPosition(i).toString()
                    .equalsIgnoreCase(report.getCategory())) {
                spCategory.setSelection(i);
                break;
            }
        }
    }

    // Harita ekranını açarak yeni konum seçilmesini sağlar
    private void openMap() {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("mode", "select");
        startActivityForResult(i, REQ_PICK_LOCATION);
    }

    // Haritadan dönen konum bilgisini alır
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQ_PICK_LOCATION) {
            selectedLat = data.getDoubleExtra("lat", 0);
            selectedLng = data.getDoubleExtra("lng", 0);
            etLocation.setText("Lat: " + selectedLat + " / Lng: " + selectedLng);
        }
    }

    // Güncellenmiş rapor bilgilerini veritabanına kaydeder
    private void save() {
        String title = etTitle.getText().toString().trim();
        String desc  = etDescription.getText().toString().trim();
        String cat   = spCategory.getSelectedItem().toString();

        // Zorunlu alan kontrolü
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Alanlar boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Güncellenmiş rapor nesnesi oluşturulur
        Report updated = new Report(
                report.getId(),
                title,
                desc,
                cat,
                "",
                report.getPhoto(),
                report.getStatus(),
                report.getCreatedBy(),
                report.getDate(),
                selectedLat,
                selectedLng
        );

        // Oturumdaki kullanıcı email’i alınır
        String email = SessionManager.getUserEmail(this);

        // Güncelleme yetkisi kontrol edilerek işlem yapılır
        if (db.updateReport(updated, email)) {
            Toast.makeText(this, "Bildirim güncellendi", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Yetkin yok", Toast.LENGTH_SHORT).show();
        }
    }
}
