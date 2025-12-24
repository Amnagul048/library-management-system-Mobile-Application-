package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    RadioButton rbStudent, rbLibrarian;
    Button btnRegister;
    TextView txtBackToLogin, txtStatus;

    FirebaseAuth auth;
    DatabaseReference usersRef;

    // IMPORTANT: paste your Realtime DB URL here from Firebase Console
    // Realtime Database → Data → URL shown at top
    private static final String DB_URL = "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        rbStudent = findViewById(R.id.rbStudent);
        rbLibrarian = findViewById(R.id.rbLibrarian);
        btnRegister = findViewById(R.id.btnRegister);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);
        txtStatus = findViewById(R.id.txtStatus);

        auth = FirebaseAuth.getInstance();

        // Force correct database instance
        FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);
        usersRef = db.getReference("users");

        btnRegister.setOnClickListener(v -> registerUser());

        txtBackToLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        txtStatus.setText("Ready to register.");
    }

    private void setStatus(String msg) {
        txtStatus.setText(msg);
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = rbLibrarian.isChecked() ? "librarian" : "student";

        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Required"); return; }

        btnRegister.setEnabled(false);
        setStatus("Creating account (Auth)...");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        btnRegister.setEnabled(true);
                        setStatus("Auth failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown"));
                        return;
                    }

                    String uid = task.getResult().getUser().getUid();
                    setStatus("Auth OK. Writing profile to Realtime DB... UID=" + uid);

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("email", email);
                    userMap.put("role", role);

                    usersRef.child(uid).setValue(userMap, (error, ref) -> {
                        btnRegister.setEnabled(true);

                        if (error != null) {
                            setStatus("DB write failed: " + error.getMessage());
                            return;
                        }

                        setStatus("DB write OK. Opening dashboard...");

                        if ("librarian".equals(role)) {
                            startActivity(new Intent(this, LibrarianHomeActivity.class));
                        } else {
                            startActivity(new Intent(this, StudentHomeActivity.class));
                        }
                        finish();
                    });
                });
    }
}
