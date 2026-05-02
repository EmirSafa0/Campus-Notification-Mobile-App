package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private TextView tvForgotMessage;
    private Button btnForgotSend;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = new DatabaseHelper(this);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        tvForgotMessage = findViewById(R.id.tvForgotMessage);
        btnForgotSend = findViewById(R.id.btnForgotSend);

        // Geri butonu
        findViewById(R.id.btnBackForgot).setOnClickListener(v -> finish());

        btnForgotSend.setOnClickListener(v -> {
            String email = etForgotEmail.getText().toString().trim();

            tvForgotMessage.setVisibility(View.GONE);

            if (email.isEmpty()) {
                tvForgotMessage.setText("Lütfen e-posta adresinizi giriniz.");
                tvForgotMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Email sistemde kayıtlı mı?
            boolean exists = db.isEmailExists(email);

            if (exists) {
                tvForgotMessage.setText("E-posta adresinize sıfırlama bağlantısı gönderildi.");
                tvForgotMessage.setTextColor(0xFF2E7D32); // yeşil
                tvForgotMessage.setVisibility(View.VISIBLE);
            } else {
                tvForgotMessage.setText("Bu e-posta adresi sistemde kayıtlı değil.");
                tvForgotMessage.setTextColor(0xFFD32F2F); // kırmızı
                tvForgotMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}
