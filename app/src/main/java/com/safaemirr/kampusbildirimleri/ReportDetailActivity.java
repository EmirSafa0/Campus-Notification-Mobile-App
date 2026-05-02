package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;

/*
 * Bu activity bir bildirimin detaylarını gösterir.
 * Kullanıcı rolüne göre düzenleme, silme, durum değiştirme
 * ve takip etme işlemleri yapılabilir.
 * Ayrıca bildirime ait konum mini harita üzerinde gösterilir.
 */
public class ReportDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    // Ekranda gösterilen metin alanları
    TextView txtTitle, txtCategory, txtLocation, txtDescription, txtStatus, txtDate;
    TextView btnBackDetail;

    // Aksiyon butonları
    Button btnSetPending, btnSetSolved, btnSetOpen;
    Button btnDelete, btnEdit, btnFollow;

    // Bildirime ait fotoğraf
    ImageView imgPhoto;

    // Mini harita nesnesi
    private GoogleMap miniMap;

    // Veritabanı ve gösterilen rapor
    DatabaseHelper db;
    Report report;

    // Kullanıcının bildirimi takip durumu
    boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        // Veritabanı bağlantısı
        db = new DatabaseHelper(this);

        // View’lar bağlanır ve rapor yüklenir
        bindViews();
        loadReport();

        if (report == null) return;

        // Sayfa içeriği ve kontroller ayarlanır
        loadDetails();
        setupRoleControl();
        setupFollowControl();
        setupActions();
    }

    // XML bileşenleri Java tarafına bağlanır
    private void bindViews() {

        txtTitle       = findViewById(R.id.txtTitle);
        txtCategory    = findViewById(R.id.txtCategory);
        txtLocation    = findViewById(R.id.txtLocation);
        txtDescription = findViewById(R.id.txtDescription);
        txtStatus      = findViewById(R.id.txtStatus);
        txtDate        = findViewById(R.id.txtDate);

        imgPhoto       = findViewById(R.id.imgPhotoReport);

        btnSetPending  = findViewById(R.id.btnSetPending);
        btnSetSolved   = findViewById(R.id.btnSetSolved);
        btnSetOpen     = findViewById(R.id.btnSetOpen);
        btnDelete      = findViewById(R.id.btnDeleteReport);

        btnEdit        = findViewById(R.id.btnEditReport);
        btnFollow      = findViewById(R.id.btnFollowReport);

        btnBackDetail  = findViewById(R.id.btnBackDetail);
    }

    // Intent ile gelen report_id’ye göre rapor bilgisi çekilir
    private void loadReport() {

        int id = getIntent().getIntExtra("report_id", -1);

        if (id == -1) {
            Toast.makeText(this, "Rapor ID alınamadı!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        report = db.getReportById(id);

        if (report == null) {
            Toast.makeText(this, "Rapor bulunamadı!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Rapor bilgileri ekrana yerleştirilir
    private void loadDetails() {

        txtTitle.setText(safe(report.getTitle()));
        txtCategory.setText(safe(report.getCategory()));
        txtDescription.setText(safe(report.getDescription()));
        txtStatus.setText(safe(report.getStatus()));
        txtDate.setText(safe(report.getDate()));
        txtLocation.setText(formatLocation());

        loadImage();
        setupMiniMap();
    }

    // Null veya boş metinleri güvenli şekilde döndürür
    private String safe(String t) {
        return (t == null || t.trim().isEmpty()) ? "—" : t;
    }

    // Konum bilgisini kullanıcıya okunabilir şekilde döndürür
    private String formatLocation() {
        if (report.getLatitude() == 0 || report.getLongitude() == 0) {
            return "Konum eklenmemiş";
        }
        return "Enlem: " + report.getLatitude() +
                "\nBoylam: " + report.getLongitude();
    }

    // Bildirime ait fotoğraf varsa yüklenir
    private void loadImage() {

        try {
            String path = report.getPhoto();

            if (path == null || path.trim().isEmpty()) {
                imgPhoto.setVisibility(View.GONE);
                return;
            }

            File f = new File(path);
            if (!f.exists()) {
                imgPhoto.setVisibility(View.GONE);
                return;
            }

            Bitmap bmp = BitmapFactory.decodeFile(path);
            imgPhoto.setImageBitmap(bmp);
            imgPhoto.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            imgPhoto.setVisibility(View.GONE);
        }
    }

    // Raporun konumu için mini harita hazırlanır
    private void setupMiniMap() {

        if (report.getLatitude() == 0 || report.getLongitude() == 0)
            return;

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.miniMap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Harita hazır olduğunda marker eklenir ve ayarlar yapılır
    @Override
    public void onMapReady(GoogleMap googleMap) {

        miniMap = googleMap;

        LatLng location = new LatLng(
                report.getLatitude(),
                report.getLongitude()
        );

        miniMap.addMarker(new MarkerOptions()
                .position(location)
                .title(report.getTitle()));

        miniMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(location, 14f));

        miniMap.getUiSettings().setScrollGesturesEnabled(false);
        miniMap.getUiSettings().setZoomControlsEnabled(false);
        miniMap.getUiSettings().setMapToolbarEnabled(false);

        // Mini haritaya tıklanınca tam harita ekranı açılır
        miniMap.setOnMapClickListener(latLng -> {

            Intent i = new Intent(this, MapActivity.class);
            i.putExtra("mode", "view");
            i.putExtra("lat", report.getLatitude());
            i.putExtra("lng", report.getLongitude());
            i.putExtra("category", report.getCategory());
            startActivity(i);
        });
    }

    // Kullanıcının rolüne göre buton görünürlükleri ayarlanır
    private void setupRoleControl() {

        String email = SessionManager.getUserEmail(this);
        if (email == null) email = "";

        String role = db.getUserRole(email);
        if (role == null) role = "";

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isOwner = email.equalsIgnoreCase(report.getUser());

        btnSetPending.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnSetSolved.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnSetOpen.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        btnEdit.setVisibility((isAdmin || isOwner) ? View.VISIBLE : View.GONE);
    }

    // Kullanıcının bildirimi takip edip etmediği kontrol edilir
    private void setupFollowControl() {

        String email = SessionManager.getUserEmail(this);
        if (email == null) {
            btnFollow.setVisibility(View.GONE);
            return;
        }

        isFollowing = db.isFollowing(report.getId(), email);
        btnFollow.setText(isFollowing ? "Takibi Bırak" : "Takip Et");
    }

    // Takip et / takibi bırak işlemi yapılır
    private void toggleFollow() {

        String email = SessionManager.getUserEmail(this);
        if (email == null) return;

        boolean ok;

        if (isFollowing) {
            ok = db.unfollowReport(report.getId(), email);
            if (ok) {
                isFollowing = false;
                btnFollow.setText("Takip Et");
            }
        } else {
            ok = db.followReport(report.getId(), email);
            if (ok) {
                isFollowing = true;
                btnFollow.setText("Takibi Bırak");
            }
        }
    }

    // Ekrandaki tüm buton aksiyonları tanımlanır
    private void setupActions() {

        btnBackDetail.setOnClickListener(v -> finish());

        btnSetPending.setOnClickListener(v -> updateStatus("İnceleniyor"));
        btnSetSolved.setOnClickListener(v -> updateStatus("Çözüldü"));
        btnSetOpen.setOnClickListener(v -> updateStatus("Açık"));

        btnDelete.setOnClickListener(v -> confirmDelete());

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, EditReportActivity.class);
            i.putExtra("report_id", report.getId());
            startActivity(i);
        });

        btnFollow.setOnClickListener(v -> toggleFollow());
    }

    // Bildirimin durumu güncellenir ve takipçilere bildirim gönderilir
    private void updateStatus(String status) {

        if (db.updateReportStatus(report.getId(), status)) {

            txtStatus.setText(status);

            ArrayList<String> followers =
                    db.getFollowersOfReport(report.getId());

            for (String email : followers) {
                NotificationHelper.send(
                        this,
                        "Takip ettiğin bildirimin durumu değişti",
                        report.getTitle() + " → " + status
                );
            }

            Toast.makeText(this, "Durum güncellendi!", Toast.LENGTH_SHORT).show();
        }
    }

    // Bildirimi silmeden önce kullanıcıdan onay alınır
    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Bildirimi Sil")
                .setMessage("Bu bildirimi silmek istiyor musun?")
                .setPositiveButton("Sil", (d, w) -> deleteReport())
                .setNegativeButton("İptal", null)
                .show();
    }

    // Bildirim veritabanından silinir
    private void deleteReport() {

        String email = SessionManager.getUserEmail(this);

        if (db.deleteReport(report.getId(), email)) {
            Toast.makeText(this, "Bildirim silindi!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        report = db.getReportById(report.getId());
        if (report != null) {
            loadDetails();
            setupRoleControl();
            setupFollowControl();
        }
    }
}
