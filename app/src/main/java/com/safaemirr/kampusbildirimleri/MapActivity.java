package com.safaemirr.kampusbildirimleri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private boolean selectMode = false;
    private boolean viewMode   = false;

    private Marker selectedMarker = null;
    private LatLng selectedLatLng = null;

    private LatLng passedLatLng = null;
    private String passedCategory = null;

    private ArrayList<Report> allReports = new ArrayList<>();
    private DatabaseHelper db;

    private static final String CAT_ALL      = "Tümü";
    private static final String CAT_SECURITY = "Güvenlik";
    private static final String CAT_LOST     = "Kayıp Eşya";
    private static final String CAT_VIOLENCE = "Şiddet / Kavga";
    private static final String CAT_THEFT    = "Hırsızlık";
    private static final String CAT_HEALTH   = "Sağlık";
    private static final String CAT_ENV      = "Çevre / Temizlik";
    private static final String CAT_TECH     = "Teknik Arıza";
    private static final String CAT_OTHER    = "Diğer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        selectMode = "select".equals(mode);
        viewMode   = "view".equals(mode);

        if (viewMode) {
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            passedCategory = intent.getStringExtra("category");
            if (lat != 0 && lng != 0) passedLatLng = new LatLng(lat, lng);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnConfirm = findViewById(R.id.btnConfirmLocation);
        btnConfirm.setVisibility(selectMode ? View.VISIBLE : View.GONE);

        btnConfirm.setOnClickListener(v -> {
            if (selectedLatLng == null) {
                Toast.makeText(this, "Konum seçiniz", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent result = new Intent();
            result.putExtra("lat", selectedLatLng.latitude);
            result.putExtra("lng", selectedLatLng.longitude);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // =============================
        // 🔍 ZOOM & GESTURE AYARLARI
        // =============================
        googleMap.getUiSettings().setZoomControlsEnabled(true);     // + -
        googleMap.getUiSettings().setZoomGesturesEnabled(true);    // pinch
        googleMap.getUiSettings().setScrollGesturesEnabled(true);  // sürükleme
        googleMap.getUiSettings().setCompassEnabled(true);         // pusula
        googleMap.getUiSettings().setMapToolbarEnabled(true);      // yol tarifi

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        LatLng atauni = new LatLng(39.898555, 41.245585);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atauni, 15f));

        enableLocationLayer();

        // -----------------------------
        // SELECT MODE
        // -----------------------------
        if (selectMode) {
            findViewById(R.id.filterScroll).setVisibility(View.GONE);
            enablePickLocationMode();
            return;
        }

        // -----------------------------
        // VIEW MODE (tek konum)
        // -----------------------------
        if (viewMode && passedLatLng != null) {
            findViewById(R.id.filterScroll).setVisibility(View.GONE);
            showSingleLocation();
            enableMarkerClickNavigation();
            return;
        }

        // -----------------------------
        // NORMAL MODE
        // -----------------------------
        ArrayList<Report> tmp = db.getAllReports();
        if (tmp != null) allReports = tmp;

        setupChipFilter();

        googleMap.clear();
        loadMarkersByCategory(CAT_ALL);

        enableMarkerClickNavigation();
    }

    // =============================
    // GERİ KALAN METOTLAR
    // =============================

    private void setupChipFilter() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
        if (chipGroup == null) return;

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID || googleMap == null) return;

            Chip chip = findViewById(checkedId);
            if (chip == null) return;

            googleMap.clear();
            loadMarkersByCategory(chip.getText().toString().trim());
        });
    }

    private void loadMarkersByCategory(String category) {
        for (Report r : allReports) {
            if (r.getLatitude() == 0 || r.getLongitude() == 0) continue;
            if (!CAT_ALL.equals(category) && !r.getCategory().equals(category)) continue;

            LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(r.getTitle())
                    .snippet(r.getCategory())
                    .icon(vectorToBitmap(this, getMarkerIcon(r.getCategory()))));
            if (m != null) m.setTag(r);
        }
    }

    private void showSingleLocation() {
        googleMap.clear();
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(passedLatLng)
                .title("Bildirilen Konum")
                .icon(vectorToBitmap(this, getMarkerIcon(passedCategory))));
        if (m != null) m.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passedLatLng, 17f));
    }

    private void enableMarkerClickNavigation() {
        googleMap.setOnInfoWindowClickListener(marker -> {
            if (marker.getTag() instanceof Report) {
                Intent i = new Intent(this, ReportDetailActivity.class);
                i.putExtra("report_id", ((Report) marker.getTag()).getId());
                startActivity(i);
            }
        });
    }

    private void enablePickLocationMode() {
        googleMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) selectedMarker.remove();
            selectedLatLng = latLng;
            selectedMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Seçilen Konum"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        });
    }

    private void enableLocationLayer() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    private int getMarkerIcon(String category) {
        switch (category) {
            case CAT_SECURITY: return R.drawable.pin_security;
            case CAT_LOST: return R.drawable.pin_lost;
            case CAT_VIOLENCE: return R.drawable.pin_violence;
            case CAT_THEFT: return R.drawable.pin_theft;
            case CAT_HEALTH: return R.drawable.pin_health;
            case CAT_ENV: return R.drawable.pin_environment;
            case CAT_TECH: return R.drawable.pin_technical;
            default: return R.drawable.pin_other;
        }
    }

    private BitmapDescriptor vectorToBitmap(Context c, int resId) {
        Drawable d = ContextCompat.getDrawable(c, resId);
        if (d == null) return null;
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        d.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(b);
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View view;
        CustomInfoWindowAdapter(Context c) {
            view = View.inflate(c, R.layout.custom_info_window, null);
        }
        @Override public View getInfoWindow(Marker marker) {
            ((TextView) view.findViewById(R.id.info_title)).setText(marker.getTitle());
            ((TextView) view.findViewById(R.id.info_snippet)).setText(marker.getSnippet());
            return view;
        }
        @Override public View getInfoContents(Marker marker) { return null; }
    }
}
