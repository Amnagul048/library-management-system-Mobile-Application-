package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView txtStatus;

    // Optional: if you have a register screen
    private Button btnRegister;

    private FirebaseAuth auth;

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtStatus = findViewById(R.id.txtStatus);

        // If your layout does not have btnRegister, comment these 2 lines
        btnRegister = findViewById(R.id.btnRegister);

        // If already logged in, route immediately
        if (auth.getCurrentUser() != null) {
            routeUserByRole();
            return;
        }

        btnLogin.setOnClickListener(v -> doLogin());

        // Optional
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v ->
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        }
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Required");
            return;
        }

        btnLogin.setEnabled(false);
        txtStatus.setText("Signing in...");

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(res -> {
                    txtStatus.setText("Login success. Checking role...");
                    routeUserByRole();
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    txtStatus.setText("Login failed: " + e.getMessage());
                });
    }

    private void routeUserByRole() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // Shouldn't happen, but fallback
            btnLogin.setEnabled(true);
            txtStatus.setText("Not logged in.");
            return;
        }

        DatabaseReference roleRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(user.getUid())
                .child("role");

        roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (role == null) role = "student";
                role = role.trim().toLowerCase();

                Intent i;
                if ("admin".equals(role)) {
                    i = new Intent(LoginActivity.this, AdminHomeActivity.class);
                } else if ("librarian".equals(role)) {
                    i = new Intent(LoginActivity.this, LibrarianHomeActivity.class);
                } else {
                    i = new Intent(LoginActivity.this, StudentHomeActivity.class);
                }

                // Clear back stack
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // If role read fails, fallback to student to avoid blocking login
                Intent i = new Intent(LoginActivity.this, StudentHomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
