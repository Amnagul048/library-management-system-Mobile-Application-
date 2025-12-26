package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Button btnAddLibrarian = findViewById(R.id.btnAddLibrarian);
        Button btnViewUsers = findViewById(R.id.btnViewUsers);
        Button btnStats = findViewById(R.id.btnStats);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnReports = findViewById(R.id.btnReports);

        btnReports.setOnClickListener(v ->
                startActivity(new Intent(AdminHomeActivity.this, AdminReportsActivity.class))
        );

        btnAddLibrarian.setOnClickListener(v ->
                startActivity(new Intent(this, AddLibrarianActivity.class)));

        btnViewUsers.setOnClickListener(v ->
                startActivity(new Intent(this, ViewUsersActivity.class)));

        btnStats.setOnClickListener(v ->
                startActivity(new Intent(this, AdminStatsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
