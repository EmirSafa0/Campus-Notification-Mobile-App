package com.safaemirr.kampusbildirimleri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Bu activity kullanıcıların kampüs içerisinde bir bildirim (report)
 * oluşturmasını sağlar. Kullanıcı başlık, açıklama, kategori, konum
 * ve isteğe bağlı olarak fotoğraf ekleyebilir.
 */
public class AddReportActivity extends AppCompatActivity {

    // Kullanıcıdan alınan metin alanları
    EditText etTitle, etDescription, etLocation;

    // Bildirim kategorisi seçimi için spinner
    Spinner spCategory;

    // Fotoğraf durumu ve önizleme alanı
    TextView txtPhotoStatus;
    ImageView imgPreview;

    // Buton ve tıklanabilir view’lar
    View btnPickLocation;
    View btnAttachPhoto;
    Button btnSubmit;

    // Veritabanı yardımcı sınıfı
    DatabaseHelper db;

    // Seçilen konum bilgileri
    Double selectedLat = null, selectedLng = null;

    // Seçilen fotoğrafın internal storage yolu
    String selectedPhotoPath = null;

    // Activity result request kodları
    private static final int REQ_PICK_LOCATION = 1001;
    private static final int REQ_PICK_IMAGE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        // Veritabanı nesnesi oluşturulur
        db = new DatabaseHelper(this);

        // XML bileşenleri Java tarafında bağlanır
        etTitle        = findViewById(R.id.etReportTitle);
        etDescription  = findViewById(R.id.etReportDescription);
        etLocation     = findViewById(R.id.etReportLocation);
        spCategory     = findViewById(R.id.spReportCategory);
        txtPhotoStatus = findViewById(R.id.txtPhotoStatus);
        imgPreview     = findViewById(R.id.imgPreview);

        btnPickLocation = findViewById(R.id.btnPickLocation);
        btnAttachPhoto  = findViewById(R.id.btnAttachPhoto);
        btnSubmit       = findViewById(R.id.btnSubmitReport);

        // Geri butonu activity’i kapatır
        findViewById(R.id.btnBackReport).setOnClickListener(v -> finish());

        // Spinner için kategori listesi yüklenir
        loadCategories();

        // Konum seçme ekranını açar
        btnPickLocation.setOnClickListener(v -> openMapToSelectLocation());

        // Galeriden fotoğraf seçmeyi başlatır
        btnAttachPhoto.setOnClickListener(v -> selectImage());

        // Bildirim kaydetme işlemi
        btnSubmit.setOnClickListener(v -> saveReport());

        // Gerekli izinler istenir
        requestPermissions();
    }

    // Konum ve galeri erişimi için runtime izinleri ister
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                101
        );
    }

    // Bildirim kategorilerini spinner’a yükler
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

    // Harita ekranını açarak konum seçilmesini sağlar
    private void openMapToSelectLocation() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("mode", "select");
        startActivityForResult(intent, REQ_PICK_LOCATION);
    }

    // Galeriden fotoğraf seçmek için intent açar
    private void selectImage() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    // Seçilen fotoğrafı uygulamanın internal storage alanına kopyalar
    private String copyImageToInternalStorage(Uri uri) {
        try {
            String fileName = "report_" + System.currentTimeMillis() + ".jpg";

            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            inputStream.close();
            outputStream.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Harita veya galeri activity’lerinden dönen sonuçları yakalar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        // Konum seçimi sonucu
        if (requestCode == REQ_PICK_LOCATION) {
            selectedLat = data.getDoubleExtra("lat", 0);
            selectedLng = data.getDoubleExtra("lng", 0);
            etLocation.setText("Lat: " + selectedLat + " / Lng: " + selectedLng);
        }

        // Fotoğraf seçimi sonucu
        if (requestCode == REQ_PICK_IMAGE) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedPhotoPath = copyImageToInternalStorage(uri);
                imgPreview.setImageURI(uri);
                imgPreview.setVisibility(View.VISIBLE);
                txtPhotoStatus.setText("📌 Fotoğraf seçildi");
            }
        }
    }

    // Girilen bilgileri kontrol edip bildirimi veritabanına kaydeder
    private void saveReport() {

        String title    = etTitle.getText().toString().trim();
        String desc     = etDescription.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        // Başlık ve açıklama boş olamaz
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Başlık ve açıklama zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Konum seçilmiş mi kontrol edilir
        if (selectedLat == null || selectedLng == null) {
            Toast.makeText(this, "Konum seçiniz.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kullanıcı oturumu kontrol edilir
        String email = SessionManager.getUserEmail(this);
        if (email == null) {
            Toast.makeText(this, "Oturum bulunamadı, tekrar giriş yapınız!", Toast.LENGTH_LONG).show();
            return;
        }

        // Bildirim tarihi oluşturulur
        String date = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        // Fotoğraf varsa yolu, yoksa boş string
        String photo = (selectedPhotoPath == null) ? "" : selectedPhotoPath;

        // Bildirim veritabanına eklenir
        boolean inserted = db.addReport(
                title,
                desc,
                category,
                "",
                photo,
                "Açık",
                email,
                date,
                selectedLat,
                selectedLng
        );

        // Sonuç kullanıcıya bildirilir
        if (inserted) {
            Toast.makeText(this, "Bildirim başarıyla gönderildi!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Bir hata oluştu, tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
        }
    }
}
