package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword, tvError;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kullanıcı zaten giriş yaptıysa → Ana sayfaya gönder
        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        // === UI Elemanları ===
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvError = findViewById(R.id.tvError);

        // === Login butonu ===
        btnLogin.setOnClickListener(v -> doLogin());

        // === Kayıt ol ===
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // === Şifre sıfırlama ===
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tvError.setVisibility(View.GONE);

        if (email.isEmpty() || password.isEmpty()) {
            tvError.setText("Lütfen e-posta ve şifrenizi giriniz.");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // === Kullanıcı bilgisi doğru mu? ===
        boolean exists = db.checkUser(email, password);

        if (exists) {

            // Kullanıcının rolünü çek
            String role = db.getUserRole(email);

            // === Session kaydet ===
            SessionManager.saveUser(this, email, role);

            // === Ana sayfaya geç ===
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        } else {
            tvError.setText("E-posta veya şifreniz hatalı.");
            tvError.setVisibility(View.VISIBLE);
        }
    }
}
