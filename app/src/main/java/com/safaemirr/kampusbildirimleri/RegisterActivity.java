package com.safaemirr.kampusbildirimleri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etUnit;
    private Button btnRegister;
    private TextView tvError;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUnit = findViewById(R.id.etUnit);
        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvError);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        tvError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(unit)) {
            tvError.setText("Lütfen tüm alanları doldurunuz.");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        if (db.isEmailExists(email)) {
            tvError.setText("Bu e-posta zaten kayıtlı.");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        boolean inserted = db.addUser(name, email, password, unit);

        if (inserted) {
            Toast.makeText(this, "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            tvError.setText("Kayıt sırasında hata oluştu.");
            tvError.setVisibility(View.VISIBLE);
        }
    }
}
