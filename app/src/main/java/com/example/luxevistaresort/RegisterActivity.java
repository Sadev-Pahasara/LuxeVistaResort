package com.example.luxevistaresort;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etMobile, etUsername, etPassword, etConfirmPassword;
    private ImageView imgTogglePassword, imgToggleConfirmPassword;
    private Button btnRegister;
    private boolean passVisible = false;
    private boolean confirmVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Password toggle
        imgTogglePassword.setOnClickListener(v -> {
            passVisible = !passVisible;
            togglePassword(etPassword, imgTogglePassword, passVisible);
        });
        imgToggleConfirmPassword.setOnClickListener(v -> {
            confirmVisible = !confirmVisible;
            togglePassword(etConfirmPassword, imgToggleConfirmPassword, confirmVisible);
        });
        btnRegister.setOnClickListener(v -> handleRegister());
    }
    private void togglePassword(EditText et, ImageView icon, boolean visible) {
        if (visible) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_off);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye);
        }
        et.setSelection(et.getText().length());
    }
    private void handleRegister() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty()) { etName.setError("Enter your name"); return; }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Enter valid email"); return; }
        if (mobile.isEmpty()) { etMobile.setError("Enter mobile number"); return; }
        if (username.isEmpty()) { etUsername.setError("Enter username"); return; }
        if (password.length() < 6) { etPassword.setError("Password must be at least 6 characters"); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Passwords do not match"); return; }

        DBHelper db = new DBHelper(this);
        if (db.userExists(username, email)) {
            Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean success = db.registerVisitor(name, email, mobile, username, password);
        if (success) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
        }
    }
}