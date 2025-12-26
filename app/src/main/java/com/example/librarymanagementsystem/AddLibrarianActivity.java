package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddLibrarianActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_librarian);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        TextView txtStatus = findViewById(R.id.txtStatus);
        Button btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.length() < 6) {
                txtStatus.setText("Invalid input");
                return;
            }

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(res -> {
                        String uid = res.getUser().getUid();

                        Map<String, Object> data = new HashMap<>();
                        data.put("email", email);
                        data.put("role", "librarian");

                        FirebaseDatabase.getInstance(DB_URL)
                                .getReference("users")
                                .child(uid)
                                .setValue(data);

                        txtStatus.setText("Librarian created successfully");
                    })
                    .addOnFailureListener(e ->
                            txtStatus.setText(e.getMessage()));
        });
    }
}
