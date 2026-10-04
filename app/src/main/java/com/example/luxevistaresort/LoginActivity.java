package com.example.luxevistaresort;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ImageView imgTogglePassword;
    private Button btnLogin;
    private TextView tvRegister;

    private boolean passwordVisible = false;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Eye toggle
        imgTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;

            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_eye);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnLogin.setOnClickListener(v -> doLogin());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void doLogin() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // ADMIN login
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            saveUserSession("ADMIN", username, "Admin");
            Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            finish();
            return;
        }

        // VISITOR/USER login
        DBHelper db = new DBHelper(this);
        String role = db.loginUser(username, password);

        if (role == null) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = db.getFullNameByUsername(username);
        saveUserSession(role, username, fullName);

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void saveUserSession(String role, String username, String fullName) {
        SharedPreferences sp = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        sp.edit()
                .putString("ROLE", role)
                .putString("USERNAME", username)
                .putString("FULL_NAME", fullName == null ? "" : fullName)
                .apply();
    }
}