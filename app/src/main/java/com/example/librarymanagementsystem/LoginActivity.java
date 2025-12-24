package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView txtRegister, txtStatus;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    // IMPORTANT: use the SAME DB URL you used in RegisterActivity
    private static final String DB_URL = "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtStatus = findViewById(R.id.txtStatus);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);
        usersRef = db.getReference("users");

        txtStatus.setText("Ready to login.");

        btnLogin.setOnClickListener(v -> doLogin());

        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void setStatus(String msg) {
        txtStatus.setText(msg);
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(pass)) { etPassword.setError("Required"); return; }

        btnLogin.setEnabled(false);
        setStatus("Signing in...");

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        btnLogin.setEnabled(true);
                        setStatus("Login failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown"));
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();
                    setStatus("Login OK. Loading profile... UID=" + uid);

                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            btnLogin.setEnabled(true);

                            if (!snapshot.exists()) {
                                auth.signOut();
                                setStatus("No profile found in DB. Please register again.");
                                return;
                            }

                            String role = snapshot.child("role").getValue(String.class);
                            if (role == null) role = "student";

                            setStatus("Profile loaded. Opening " + role + " dashboard...");

                            if ("librarian".equalsIgnoreCase(role)) {
                                startActivity(new Intent(LoginActivity.this, LibrarianHomeActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, StudentHomeActivity.class));
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            btnLogin.setEnabled(true);
                            setStatus("Profile load failed: " + error.getMessage());
                        }
                    });
                });
    }
}
